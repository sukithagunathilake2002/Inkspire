package com.inkspire.inkspire.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "*")
public class UploadController {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostMapping
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body("No file uploaded.");
            }
    
            // Normalize upload directory
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);
    
            // Sanitize filename
            String originalName = file.getOriginalFilename();
            String cleanName = originalName != null ? originalName.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_") : "file";
            String filename = System.currentTimeMillis() + "_" + cleanName;
            Path filepath = uploadPath.resolve(filename);
    
            // Save the file
            Files.copy(file.getInputStream(), filepath, StandardCopyOption.REPLACE_EXISTING);
    
            // Return the URL path
            return ResponseEntity.ok("/media/" + filename);
    
        } catch (IOException e) {
            e.printStackTrace(); // Show real error in console
            return ResponseEntity.status(500).body("Upload failed: " + e.getMessage());
        }
    }
    
}