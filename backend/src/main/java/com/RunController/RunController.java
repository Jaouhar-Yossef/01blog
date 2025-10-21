package com.RunController;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController


public class RunController {

    @GetMapping("/")
    public String home() {
        return "Welcome to my Spring Boot API!";
    }

    @GetMapping("/hello")
    public String sayHello() {
        return "Hello from RunController!";
    }


}