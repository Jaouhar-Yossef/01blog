package com.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.entity.User;
import com.service.UserService;
import java.util.Map;

import com.util.Response;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    
    @GetMapping("/check")
    public Map<String, Boolean> checkUser() {
        System.err.println("hhhhhhhhhhhhhhhhh  >>>>>>>>>>>>>>>>");
        boolean isRegistered = true; 
        return Map.of("registered", isRegistered);
    }

    @PostMapping("/register")
    public ResponseEntity<Response> register(@RequestBody User user) {

        Response response = userService.register(user);

        if (!response.isSuccess()) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(response);
        }
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    @PostMapping("/login")
    public ResponseEntity<Response> login(@RequestBody LoginRequest request) {
        Response response = userService.login(request.getEmail(), request.getPassword());
        if (!response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        return ResponseEntity.ok(response);
    }


}
