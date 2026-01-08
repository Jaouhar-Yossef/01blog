package com.service;

import org.springframework.stereotype.Service;
import com.entity.User;
import com.repository.UserRepository;

import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.util.Response;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public Response register(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return new Response(false , "Email already exists");
        }

        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return new Response(true ,  "User registered successfully: " + user.getUsername());
    }

    public Response login(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return new Response(false, "User not found");
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return new Response(false, "Invalid password");
        }

        return new Response(true, "Login successful");
    }

}
