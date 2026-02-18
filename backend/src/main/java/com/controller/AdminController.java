package com.controller;

import org.hibernate.sql.Update;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dto.UpdateReportsRequest;
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
            Response<?> data = adminService.getReports(page, size);
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

    @GetMapping("getUsers")
    private ResponseEntity<Response<?>> getAllUsers(@RequestParam int page,
            @RequestParam int size) {
        try {
            Response<?> data = adminService.getUsers(page, size);
            return ResponseEntity.accepted().body(data);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(false, e.getMessage()));
        }
    }

    @GetMapping("getBlogs")
    private ResponseEntity<Response<?>> getAllBlogs(@RequestParam int page,
            @RequestParam int size) {
        try {
            Response<?> data = adminService.getBlogs(page, size);
            return ResponseEntity.accepted().body(data);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(false, e.getMessage()));
        }
    }

    @PutMapping("updateReportStatus")
    private ResponseEntity<Response<?>> updateReportStatus(@RequestBody UpdateReportsRequest request) {
        try {
            Response<?> data = adminService.updateReport(request);
            return ResponseEntity.accepted().body(data);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(false, e.getMessage()));
        }
    }

    @PutMapping("updateBlogStatus")
    private ResponseEntity<Response<?>> updateBlogStatus(@RequestBody UpdateReportsRequest request) {
        try {
            Response<?> data = adminService.updateBlog(request);
            return ResponseEntity.accepted().body(data);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(false, e.getMessage()));
        }
    }

    @DeleteMapping("deleteReport")
    private ResponseEntity<Response<?>> deleteReport(@RequestBody UpdateReportsRequest request) {
        try {
            boolean data = adminService.deleteReport(request);
            return ResponseEntity.accepted().body(new Response<>(data, ""));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(false, e.getMessage()));
        }
    }
}
