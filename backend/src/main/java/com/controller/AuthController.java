package com.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.dto.Request.LoginRequest;
import com.dto.Request.UpdateProfile;
import com.dto.Request.UserRequestDTO;
import com.dto.Response.UserResponseDTO;
import com.dto.Response.ValidationDTO;
import com.entity.User;
import com.entity.UserDetailsImpl;
import com.service.UserService;

import com.util.Response;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    @GetMapping("/getTheDataUser")
    private ResponseEntity<Response<?>> getDataProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        try {

            User user = userDetails.getUser();
            UserResponseDTO Profile = new UserResponseDTO(
                    user.getUsername(),
                    user.getEmail(),
                    user.getImageUrl(),
                    user.getRole(),
                    user.getStatus(),
                    "");
            return ResponseEntity.badRequest().body(new Response<>(true, "" , Profile));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(false, e.getMessage()));
        }
    }

    @PutMapping("/updateProfile")
    private ResponseEntity<Response<?>> updateProfile(@Valid @ModelAttribute UpdateProfile updateProfile,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(new Response<>(false, errorMsg));
        }

        try {
            UUID user_id = userDetails.getUser().getId();
            Response<?> response = userService.updateProfile(user_id, updateProfile);
            if (response.isSuccess()) {
                return ResponseEntity.accepted().body(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(false, e.getMessage()));
        }
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
