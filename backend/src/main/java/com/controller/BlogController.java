package com.controller;

import com.dto.BlogRequest;
import com.dto.BlogResponseDTO;
import com.service.BlogService;
import org.springframework.http.MediaType;
import com.util.Response;

import org.springframework.http.ResponseEntity;
import org.springframework.security .core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/blogs")
public class BlogController {

    private final BlogService blogService;

    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    @PostMapping("/All-blogs")
    public String Blogs(Authentication authentication) {
        return "not now";
    }

    @PostMapping(value = "/creat-blog", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Response<BlogResponseDTO>> createBlog( @ModelAttribute BlogRequest blogRequest, Authentication authentication) {
        String username = authentication.getName();
        Response<BlogResponseDTO> data = blogService.createBlog(blogRequest, username);
        return ResponseEntity.ok(data);
    }

}