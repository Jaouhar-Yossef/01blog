package com.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.dto.Request.NotificationRequest;
import com.dto.Request.NotificationsList_Request;

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

    @PutMapping("readAllNotifications")
    private ResponseEntity<Response<?>> readAllNotifications(
            @RequestBody @Valid NotificationsList_Request request,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(new Response<>(false, errorMsg));
        }

        try {
            UUID userId = userDetails.getUser().getId();
            notificationsService.ReadAllNotifications(request.getNotificationIds(), userId);
            return ResponseEntity.ok(new Response<>(true, ""));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(false, e.getMessage()));
        }
    }

    @PutMapping("readNotification")
    private ResponseEntity<Response<?>> readNotification(
            @RequestBody @Valid NotificationRequest request,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(new Response<>(false, errorMsg));
        }

        try {
            UUID userId = userDetails.getUser().getId();
            notificationsService.ReadNotification(request.getId_Notification(), userId);
            return ResponseEntity.ok(new Response<>(true, ""));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(false, e.getMessage()));
        }
    }

    @PostMapping("/deleteAllNotifications")
    private ResponseEntity<Response<?>> deleteAllNotifications(
            @RequestBody @Valid NotificationsList_Request request,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(new Response<>(false, errorMsg));
        }

        try {
            UUID userId = userDetails.getUser().getId();
            notificationsService.DeleteAllNotifications(request.getNotificationIds(), userId);
            return ResponseEntity.ok(new Response<>(true, ""));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(false, e.getMessage()));
        }
    }

    @DeleteMapping("/deleteOneNotification/{Id}")
    private ResponseEntity<Response<?>> deleteOneNotification(@PathVariable Long Id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
                
        try {
            UUID userId = userDetails.getUser().getId();
            Response<?> data = notificationsService.deleteNotification(Id, userId);
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
