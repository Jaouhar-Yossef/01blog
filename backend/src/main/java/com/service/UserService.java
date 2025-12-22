package com.service;

import org.springframework.stereotype.Service;
import com.entity.User;
import com.repository.UserRepository;
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
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return new Response(true ,  "User registered successfully: " + user.getUsername());
    }
}
