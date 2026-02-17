package org.example._03authservice.service;
import org.example._03authservice.model.User;
import org.example._03authservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private PasswordEncoder passwordEncoder; // Uses the tool from your config folder

    @Autowired
    private UserRepository userRepository;

    public User register(User user) {

        /// check if username exists before registration
        // This is where 'password123' becomes a secure hash!
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
}