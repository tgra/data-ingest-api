package com.example.filedemo.payload;


public class UploadFileResponse {
    private String fileName; 
    
    
 
    public UploadFileResponse(String fileName) {
        this.fileName = fileName;
        
        
    }

    
    public String getValue() {
        return this.fileName;
        }
        
}