package org.example._5fileservice.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;

@Service
public class FileStorageService {

    // This points to the "uploads" folder you showed in your first image
    private final Path root = Paths.get("uploads");

    public void save(MultipartFile file, String username) {
        try {
            // This creates the user-specific path: uploads/john123
            Path userPath = this.root.resolve(username);

            // If the folder for this specific user doesn't exist, create it
            if (!Files.exists(userPath)) {
                Files.createDirectories(userPath);
            }

            // Copy the file into that user's folder
            Files.copy(file.getInputStream(), userPath.resolve(file.getOriginalFilename()), StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }
}