package com.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.dto.LoginRequest;
import com.dto.UserRequestDTO;
import com.dto.UserResponseDTO;
import com.dto.ValidationDTO;
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
    public ResponseEntity<Response<?>> register(@RequestBody @Valid UserRequestDTO request,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(new Response<>(false, errorMsg));
        }
        try {
            Response<?> response = userService.register(request);
            if (!response.isSuccess()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(false, e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Response<?>> login(@RequestBody @Valid LoginRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(new Response<>(false, errorMsg));
        }
        try {
            Response<UserResponseDTO> response = userService.login(request.getEmailOrUsername(), request.getPassword());
            if (!response.isSuccess()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(false, e.getMessage()));
        }

    }

    @DeleteMapping("/deleteAccount")
    public ResponseEntity<?> delete_Account(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            UUID user_id = userDetails.getUser().getId();
            Response<?> response = userService.deleteAccount(user_id);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new Response<>(false, e.getMessage()));
        }
    }

    @PostMapping("/validate-token")
    public ResponseEntity<Response<?>> validateToken(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Response<>(false, null));
        }
        try {
            UUID user_id = userDetails.getUser().getId();
            ValidationDTO dataUser = userService.getDataUser(user_id);
            return ResponseEntity.accepted().body(new Response<>(true, "successful", dataUser));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Response<>(false, e.getMessage()));

        }

    }

}
