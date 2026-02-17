package org.example._03authservice.controller;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.example._03authservice.model.User;
import org.example._03authservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager; // Handles the login check
    @Autowired
    private UserRepository userRepository; // You'll need to autowire your repository

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@RequestBody User user) {
        // 1. Check if user already exists
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("Error","Username already taken",null));
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 3. Save the new user to the database
        userRepository.save(user);

        return ResponseEntity.ok(new ApiResponse<>("Success","User registered successfully","User.getUsername"));
    }
        @PostMapping("/login")
        public ResponseEntity<ApiResponse<String>> login(@RequestBody User loginRequest)
        {
            try {
                // 1. Authenticate the user
                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginRequest.getUsername(),
                                loginRequest.getPassword()
                        )
                );
                String secretString = "ThisIsASecretKeyThatIsExactlySixtyFourCharactersLongToFixTheError";
                Key key = Keys.hmacShaKeyFor(secretString.getBytes());

                long now = System.currentTimeMillis();
                long dayInMs = 24 * 60 * 60 * 1000; // 86,400,000 milliseconds for 24 hours

                String jwtToken = Jwts.builder()
                        .setSubject(loginRequest.getUsername())
                        .setIssuedAt(new Date(now))
                        .setExpiration(new Date(now + dayInMs))
                        .signWith(key, SignatureAlgorithm.HS256)
                        .compact();
                return ResponseEntity.ok(new ApiResponse<>("Success","Login successful",jwtToken));

            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(401).body(new ApiResponse<>("Error","Invalid username or password",null));
            }
        }
    }
