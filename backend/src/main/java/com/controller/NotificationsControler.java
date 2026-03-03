package com.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dto.Request.PageableDTO;
import com.entity.UserDetailsImpl;
import com.service.NotificationsService;
import com.util.Response;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/Notifications")
@RequiredArgsConstructor
public class NotificationsControler {

    private final NotificationsService notificationsService;

    @DeleteMapping("/deleteOneNotification/{Id}")
    private ResponseEntity<Response<?>> deleteOneNotification(@PathVariable Long blogId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        try {

            UUID userId = userDetails.getUser().getId();
            Response<?> data = notificationsService.deleteNotification(blogId , userId);
            return ResponseEntity.accepted().body(data);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(false, e.getMessage()));
        }
    }

    @GetMapping("/getNotifications")
    private ResponseEntity<Response<?>> getTheNotifications(
            @ModelAttribute @Valid PageableDTO PageableDTO,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(new Response<>(false, errorMsg));
        }

        try {
            UUID userId = userDetails.getUser().getId();
            Response<?> data = notificationsService.getNotifications(userId, PageableDTO.getPage(),
                    PageableDTO.getSize());
            return ResponseEntity.accepted().body(data);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(false, e.getMessage()));
        }
    }
}
