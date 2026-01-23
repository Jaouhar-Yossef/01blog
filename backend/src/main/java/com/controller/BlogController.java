package com.controller;

import com.dto.BlogRequest;
import com.dto.BlogResponseDTO;
import com.service.BlogService;
import com.util.Response;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/blogs")
public class BlogController {

    private final BlogService blogService;

    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    @PostMapping("/creat-blog")
    public ResponseEntity<Response<BlogResponseDTO>> createBlog(@RequestBody BlogRequest blogRequest, Authentication authentication) {
        String username = authentication.getName();
        Response<BlogResponseDTO> data =  blogService.createBlog(blogRequest, username);
        return ResponseEntity.ok(data);
    }

}