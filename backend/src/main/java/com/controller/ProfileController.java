package com.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dto.Response.ProfileResponseDTO;
import com.entity.UserDetailsImpl;
import com.service.ProfileService;
import com.util.Response;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/profile")
public class ProfileController {
    
    private final ProfileService ProfileService;

    @GetMapping("{username}")
    private ResponseEntity<Response<?>> getProfile(@PathVariable String username,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            UUID user_id = userDetails.getUser().getId();
            ProfileResponseDTO userData = ProfileService.getProfileData(username, user_id);
            return ResponseEntity.ok(new Response<>(true, "get data Profile sucesfuly", userData));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(false, e.getMessage()));
        }
    }

    @PostMapping("{username}/follow")
    private ResponseEntity<Response<?>> follow(@PathVariable String username,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            UUID user_id = userDetails.getUser().getId();
            this.ProfileService.followed(username, user_id);
            return ResponseEntity.accepted().body(new Response<>(true, "followed sucesfuly"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(false, e.getMessage()));
        }
    }

    @PostMapping("{username}/unfollow")
    private ResponseEntity<Response<?>> unFollow(@PathVariable String username,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            UUID user_id = userDetails.getUser().getId();
            this.ProfileService.unFollow(username, user_id);
            return ResponseEntity.accepted().body(new Response<>(true, "UnFollowed sucesfuly"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(false, e.getMessage()));
        }
    }
}
