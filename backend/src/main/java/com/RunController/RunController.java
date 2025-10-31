package com.RunController;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RunController {

    @GetMapping("/")
    public String home() {
        return "Welcome to my Spring Boot API!";
    }

    @GetMapping("/api/message")
    public String sayHello() {
        return "Hello from RunController server!";
    }


    @PostMapping("/auth/register")
    public Map<String, String> registerUser(@RequestBody UserDTO user) {
        System.out.println("Received user: " + user.getUsername() + ", " + user.getEmail());
        return Map.of("message", "User registered successfully: " + user.getUsername());
    }
}