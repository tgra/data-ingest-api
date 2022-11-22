package com.example.filedemo.service;

import com.example.filedemo.exception.FileStorageException;
import com.example.filedemo.exception.MyFileNotFoundException;
import com.example.filedemo.property.FileStorageProperties;

import com.example.filedemo.model.Company;
import com.example.filedemo.repository.CompanyRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class FileStorageService {

    private final Path fileStorageLocation;
    
    @Autowired    
    public FileStorageService(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }
    
    
    @Autowired
    CompanyRepository comRepository;     
	public Company createCompany(Company com) {	
	     return comRepository.save(com);
	 }

	public String extractCompanies(MultipartFile file) {
		
		String fileContents;
		HashMap<Integer,Company> companies = new HashMap<>();
		ArrayList<String> companyList = new ArrayList<String>();
		
		try {
			fileContents = this.parseFile(file);
			
			
			Pattern match_company_name = Pattern.compile("(([\\(\\)&A-Z0-9\\/-]+ )+)");

			Pattern match_company_code = Pattern.compile("(\\d{8})");
			Pattern match_event_code = Pattern.compile("([A-Z]\\d*)");
			Pattern match_date = Pattern.compile("(\\d{2}\\/\\d{2}\\/\\d{4})");
			Pattern space_plus = Pattern.compile("\\s+");
			Pattern company_divider = Pattern.compile("\\*{5,}\n");  
			Pattern company_name_extra = Pattern.compile("^\\s");
			
	        String[] str=company_divider.split(fileContents); 
	        fileContents = str[1];
	        
	        String[] lines= Pattern.compile("\n").split(fileContents);
	        
	        HashMap<String,String> company = new HashMap<String,String>();
	        
	        company.put("CompanyName", "");
	        company.put("CompanyNumber", "");
	        company.put("EventType", "");
	        company.put("EventDate", "");
	        company.put("UniqueIdentifier", "");
	        

	        
	        Integer index = 0;
	         

	        while (index < lines.length) {
	        	String company1 = lines[index].substring(10, 74);
	        	String company2 = lines[index].substring(76);
	        	companyList.add(company1);
	        	companyList.add(company2);
	        	index += 1;
	        	}
	        	
	        
	        for (int idx = 0; idx < companyList.size(); idx ++) {

	        	
      	      		// extract strings for each data component
	        		String companyString = companyList.get(idx).toString();
	        		
	        		if (companyString.length() < 64) 
	        			continue;
	        		
	        		String str_company_name = companyString.substring(0, 35);
	        		String str_company_code = companyString.substring(36, 46);
	        		String str_event_code = companyString.substring(49, 53);
	        		String str_date = companyString.substring(54, 64);
	        		
	        		Company thisCompany = new Company();

		            // unique identifier
		        	Integer uid = idx + 1;
		            thisCompany.UniqueIdentifier = uid.toString() ;

		            // match company code
		            Matcher matchCompanyCode = match_company_code.matcher(str_company_code);
		            if(matchCompanyCode.find()) {
		            	thisCompany.CompanyNumber = matchCompanyCode.group(1);
		            } else {
		            
		         // if cannot match company code, this line may contain continuation of company name
		           
		              // check for space indent in company name
		              Matcher matchCompanyNameExtra = company_name_extra.matcher(str_company_name);
		              // if additional company name append company name for this line, to previous company (x - 2)
		              if (matchCompanyNameExtra.find()) {
		            	  Integer idxu = idx - 2;
		            	  
		            	  if (companies.containsKey(idxu)){
		            		  Company com = companies.get(idxu);
		            		  com.CompanyName = com.CompanyName + " " + str_company_name.trim();
			            	  companies.put(idxu, com);
		            	  }
		                continue;
		              }

		            }
		            
		         // date
		            Matcher matchDate = match_date.matcher(str_date);
		            if (matchDate.find()) {
		              thisCompany.EventDate = matchDate.group(1);
		            }

		            // event code
		            Matcher matchCode = match_event_code.matcher(str_event_code);
		            if (matchCode.find()) {
		              thisCompany.EventType = matchCode.group(1);
		            }

		            // company name
		            // if match then add otherwise go to next 
		            Matcher matchCompanyName = match_company_name.matcher(str_company_name);
		            if (matchCompanyName.find()) {
		              thisCompany.CompanyName = matchCompanyName.group(1);
		            } else {
		              
		              continue;
		            }
		            
		            
		            companies.put(idx, thisCompany);
	        }
	        
	        for (Entry<Integer, Company> entry : companies.entrySet()) {
	            Integer key=entry.getKey();
	            Company com=entry.getValue();
	            
	            Company comtxt = this.createCompany(com);
	        }
	     
	        		
	        

		} catch (Exception ex) {
			
			 throw new FileStorageException("Could not parse file " , ex);		  
		}
		return "success";
	}
	
    public String storeFile(MultipartFile file) {
        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check if the file's name contains invalid characters
            if(fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }
    
    public String parseFile(MultipartFile file) {
    	String fileName = StringUtils.cleanPath(file.getOriginalFilename());
    	try {
    		InputStream inputStream = file.getInputStream();
    		
    		String fileContents = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    	
			return fileContents;
    	
    	} catch (IOException ex) {
            throw new FileStorageException("Could not parse file " + fileName + ". Please try again!", ex);
        }
    }

    
    
	 
	
    
    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new MyFileNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new MyFileNotFoundException("File not found " + fileName, ex);
        }
    }

    
    
	
}