package com.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.entity.UserDetailsImpl;
import com.service.UserService;
import com.util.Response;

@RestController
@RequestMapping("/profile")
public class ProfileController {

    private UserService userService;

    public ProfileController (UserService userService) {
        this.userService = userService;
    }


    @GetMapping("{username}")
    private ResponseEntity<Response<?>> getProfile(@PathVariable String username,
            @AuthenticationPrincipal UserDetailsImpl userDetails ) {
        try {
            UUID user_id = userDetails.getUser().getId();
            Response<?> userData =   this.userService.getProfileData(username  , user_id);
            return ResponseEntity.ok(userData);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(false, e.getMessage()));
        }

    }
}
