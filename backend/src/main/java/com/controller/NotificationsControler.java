package com.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.service.NotificationsService;
import com.util.Response;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/Notifications")
@RequiredArgsConstructor
public class NotificationsControler {

    private NotificationsService notificationsService;

    @GetMapping("/getNotifications")
    private ResponseEntity<Response<?>> getTheNotifications(@RequestParam int page,
            @RequestParam int size) {
        try {
            notificationsService.getNotifications(page, size);
            return ResponseEntity.accepted().body(null);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(false, e.getMessage()));
        }
    }
}
