package com.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dto.Request.DeleteReportsRequest;
import com.dto.Request.UpdateBlogsStatusRequestDTO;
import com.dto.Request.UpdateReportsRequest;
import com.dto.Request.UpdateStatusBlogRequest;
import com.entity.UserDetailsImpl;
import com.service.AdminService;
import com.service.Blogs.BlogService;
import com.util.Response;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final BlogService blogService;

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

    @GetMapping("/getAnalytics")
    private ResponseEntity<Response<?>> analytics() {
        try {
            Response<?> data = adminService.getAnalytics();
            return ResponseEntity.accepted().body(data);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(false, e.getMessage()));
        }
    }

    @GetMapping("/getUsers")
    private ResponseEntity<Response<?>> getAllUsers(@RequestParam int page,
            @RequestParam int size) {
        try {
            Response<?> data = adminService.getUsers(page, size);
            return ResponseEntity.accepted().body(data);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(false, e.getMessage()));
        }
    }

    @GetMapping("/getBlogs")
    private ResponseEntity<Response<?>> getAllBlogs(@RequestParam int page,
            @RequestParam int size) {
        try {
            Response<?> data = adminService.getBlogs(page, size);
            return ResponseEntity.accepted().body(data);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(false, e.getMessage()));
        }
    }

    @PutMapping("/updateReportStatus")
    private ResponseEntity<Response<?>> updateReportStatus(@Valid @RequestBody UpdateReportsRequest request) {
        System.out.println("==>jj");
        try {
            Response<?> data = adminService.updateReport(request);
            return ResponseEntity.accepted().body(data);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(false, e.getMessage()));
        }
    }

    @PutMapping("/updateBlogStatus")
    private ResponseEntity<Response<?>> updateBlogStatus(@Valid @RequestBody UpdateBlogsStatusRequestDTO request,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(new Response<>(false, errorMsg));
        }

        try {
            Response<?> data = adminService.updateStatusBlog(request);
            return ResponseEntity.accepted().body(data);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(false, e.getMessage()));
        }
    }

    @PutMapping("updateUserStatus")
    private ResponseEntity<Response<?>> updateUserStatus(@Valid @RequestBody UpdateStatusBlogRequest request,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(new Response<>(false, errorMsg));
        }

        try {
            Response<?> data = adminService.updateStatusUser(request);
            return ResponseEntity.accepted().body(data);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(false, e.getMessage()));
        }
    }

    @DeleteMapping("deleteReport")
    private ResponseEntity<Response<?>> deleteReport(@Valid @RequestBody DeleteReportsRequest request,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(new Response<>(false, errorMsg));
        }

        try {
            boolean data = adminService.deleteReport(request);
            return ResponseEntity.accepted().body(new Response<>(data, ""));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(false, e.getMessage()));
        }
    }

    @DeleteMapping("deleteblog/{blogId}")
    private ResponseEntity<Response<?>> deleteBlog(@PathVariable UUID blogId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            UUID user_id = userDetails.getUser().getId();
            Response<?> data = blogService.deleteOneBlog(user_id, blogId);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(false, e.getMessage()));
        }
    }

    @DeleteMapping("deleteUser/{username}")
    private ResponseEntity<Response<?>> deleteUser(@PathVariable String username,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            boolean data = adminService.deleteUser(username);
            return ResponseEntity.ok(new Response<>(data, "Delete successfully!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(false, e.getMessage()));
        }
    }

}
