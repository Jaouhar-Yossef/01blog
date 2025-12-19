package com.controller;

import org.springframework.web.bind.annotation.*;
import com.entity.User;
import com.service.UserService;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }
    
    
    @GetMapping("/auth/check")
    public Map<String, Boolean> checkUser() {
        System.err.println("hhhhhhhhhhhhhhhhh  >>>>>>>>>>>>>>>>");
        boolean isRegistered = true; 
        return Map.of("registered", isRegistered);
    }


    // @PostMapping("/register")
    // public Map<String, String> registerUser(@RequestBody User user) {
    //     String message = userService.register(user);
    //     return Map.of("message", message);
    // }
}
