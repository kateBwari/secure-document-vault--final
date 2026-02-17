package org.example._04fileservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
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
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal Object principal) {

        String username = getUsernameFromPrincipal(principal);

        // Security check: don't allow upload if user is unknown
        if (username.equals("unknown")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid User Token");
        }

        String uploadPath = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + username;

        try {
            File userFolder = new File(uploadPath);
            if (!userFolder.exists()) {
                userFolder.mkdirs();
            }

            Path destination = Paths.get(uploadPath).resolve(file.getOriginalFilename());
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

            return ResponseEntity.ok("File saved successfully in folder: " + username);

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Failed to upload file: " + e.getMessage());
        }
    }

    @DeleteMapping("/{fileName}")
    public ResponseEntity<String> deleteFile(
            @PathVariable String fileName,
            @AuthenticationPrincipal Object principal) {

        String username = getUsernameFromPrincipal(principal);
        String uploadPath = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + username;
        File fileToDelete = new File(uploadPath + File.separator + fileName);

        if (fileToDelete.exists()) {
            if (fileToDelete.delete()) {
                return ResponseEntity.ok("File deleted successfully");
            }
            return ResponseEntity.internalServerError().body("Could not delete file from disk");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found in " + username + "'s folder");
    }

    @PutMapping("/{fileName}")
    public ResponseEntity<String> updateFile(
            @PathVariable String fileName,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal Object principal) {

        // uploadFile uses REPLACE_EXISTING, so it works as an update
        return uploadFile(file, principal);
    }
    private String getUsernameFromPrincipal(Object principal) {
        if (principal instanceof JwtAuthenticationToken jwtToken) {
            return jwtToken.getTokenAttributes().get("sub").toString();
        } else if (principal instanceof Jwt jwt) {
            return jwt.getClaimAsString("sub");
        }
        return "unknown";
    }
}