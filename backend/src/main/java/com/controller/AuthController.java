package com.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dto.UserResponseDTO;
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
        // identifier can be email or username
        String emailOrUsername = request.getEmailOrUsername();
        Response<UserResponseDTO> response = userService.login(emailOrUsername, request.getPassword());
    
        if (!response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        return ResponseEntity.ok(response);
    }



    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> me(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    
        String token = authHeader.substring(7); // remove "Bearer " prefix
        UserResponseDTO user = userService.getUserFromToken(token);
    
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(user);
    }





}
