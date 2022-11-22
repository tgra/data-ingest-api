package com.example.filedemo.controller;
import com.example.filedemo.model.Company;
import com.example.filedemo.payload.UploadFileResponse;
import com.example.filedemo.service.FileStorageService;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FileStorageService fileStorageService;
    
    @PostMapping("/upload")
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file) {
        String fileName = fileStorageService.storeFile(file);

        String extractCompanies = fileStorageService.extractCompanies(file);

        return new UploadFileResponse(extractCompanies);
    }

    


}