package com.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RunController {

    @GetMapping("/")
    public String home() {
        return "Welcome to my Spring Boot API!";
    }

    @GetMapping("/auth/check")
    public Map<String, Boolean> checkUser() {
        boolean isRegistered = true; 
        return Map.of("registered", isRegistered);
    }

    @GetMapping("/api/message")
    public String sayHello() {
        return "Hello from RunController server!";
    }


    @PostMapping("/auth/register")
    public Map<String, String> registerUser() {

        System.out.println("Received user: ");

        return Map.of("message", "User registered successfully: ");
    }
}