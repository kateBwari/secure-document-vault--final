package org.example._04fileservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@RestController
@RequestMapping("/file")
public class FileController {

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<String>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("file_name") String customName,
            @AuthenticationPrincipal Object principal) {

        String username = getUsernameFromPrincipal(principal);

        if (username.equals("unknown")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("Error","Invalid User Token", null));
        }

        String uploadPath = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + username;

        try {
            File userFolder = new File(uploadPath);
            if (!userFolder.exists()) {
                userFolder.mkdirs();
            }

            Path destination = Paths.get(uploadPath).resolve(customName + ".png");
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

            return ResponseEntity.ok(new ApiResponse<>("Success","File saved successfully in folder",customName + ".png"));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>("Failed","Failed to upload file: " + e.getMessage(),null));
        }
    }

    @DeleteMapping("/{fileName}")
    public ResponseEntity<ApiResponse<String>> deleteFile(
            @PathVariable String fileName,
            @AuthenticationPrincipal Object principal) {

        // Fixed: Called locally without 'service.' prefix
        String username = getUsernameFromPrincipal(principal);
        String uploadPath = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + username;

        // Fixed: Added .png so the file can be found on disk
        System.out.println("DEBUG: Trying to delete file at: " + uploadPath + File.separator + fileName + ".png");
        File fileToDelete = new File(uploadPath + File.separator + fileName + ".png");

        if (fileToDelete.exists()) {
            if (fileToDelete.delete()) {
                return ResponseEntity.ok(new ApiResponse<>("Success","File deleted successfully", "filename"));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body( new ApiResponse<>("Failed","Could not delete file","fileName"));
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>("Failed","File not found in Folder", username + "'s folder"));
    }

    @PutMapping("/{fileName}")
    public ResponseEntity<ApiResponse<String>> updateFile(
            @PathVariable String fileName,
            @RequestParam("file") MultipartFile file,
            @RequestParam("file_name") String newCustomName,
            @AuthenticationPrincipal Object principal) {

        // Fixed: Passing 'newCustomName' to match the variable above
        return uploadFile(file, newCustomName, principal);
    }

    // Helper method to extract username from JWT
    private String getUsernameFromPrincipal(Object principal) {
        if (principal instanceof org.springframework.security.oauth2.jwt.Jwt) {
            return ((org.springframework.security.oauth2.jwt.Jwt) principal).getClaimAsString("sub");
        } else if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            return ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
        }
        return "unknown";
    }
}