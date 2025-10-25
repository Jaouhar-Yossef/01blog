package com.RunController;

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


    @PostMapping("/register")
    public String registerUser(@RequestBody UserDTO user) {
        System.out.println("Received user: " + user.getName() + ", " + user.getEmail());
        return "User registered successfully: " + user.getName();
    }

}