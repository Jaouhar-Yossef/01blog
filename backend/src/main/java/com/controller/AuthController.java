package com.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.dto.LoginRequest;
import com.dto.UserRequestDTO;
import com.dto.UserResponseDTO;
import com.entity.User;
import com.entity.UserDetailsImpl;
import com.service.UserService;

import com.util.Response;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<Response<?>> register(@RequestBody @Valid UserRequestDTO request) {

        Response<?> response = userService.register(request);
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
    public ResponseEntity<Response<?>> login(@RequestBody @Valid LoginRequest request) {       
        String emailOrUsername = request.getEmailOrUsername();
        Response<UserResponseDTO> response = userService.login(emailOrUsername, request.getPassword());
    
        if (!response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/validate-token")
    public void me(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return;
    }

    @DeleteMapping("/deleteAccount")
    public ResponseEntity<?> delete_Account(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        try {
            Response<?> response =  userService.deleteAccount(user);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new Response<>(false, e.getMessage())); 
        }
    }
}
