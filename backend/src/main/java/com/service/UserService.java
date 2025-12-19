package com.service;

import org.springframework.stereotype.Service;
import com.entity.User;
import com.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public String register(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return "Email already exists";
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "User registered successfully: " + user.getUsername();
    }
}
