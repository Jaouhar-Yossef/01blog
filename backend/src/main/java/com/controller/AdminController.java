package com.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.service.AdminService;
import com.util.Response;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }


    @GetMapping("/getRports")
    private ResponseEntity<Response<?>> getAllReports(@RequestParam int page,
            @RequestParam int size) {
        try {
            Response<?> data = adminService.getReports(page , size);
            return ResponseEntity.accepted().body(data);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(false, e.getMessage()));
        }
    }

    @GetMapping("getAnalytics")
    private ResponseEntity<Response<?>> analytics() {
        try {
            Response<?> data = adminService.getAnalytics();
            return ResponseEntity.accepted().body(data);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(false, e.getMessage()));
        }

    }
}
