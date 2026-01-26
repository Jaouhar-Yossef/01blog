package com.controller;

import com.dto.BlogRequest;
import com.dto.BlogResponseDTO;
import com.entity.Blog;
import com.service.BlogService;

import java.util.List;

import com.util.Response;

import org.springframework.http.HttpStatus;
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



    @GetMapping("/allblogs")
    public ResponseEntity<List<BlogResponseDTO>> getAllBlogs() {
        List<Blog> blogs = blogService.getAllBlogs();

        List<BlogResponseDTO> blogDTOs = blogs.stream()
                .map(blog -> new BlogResponseDTO(
                        blog.getTitle(),
                        blog.getContent(),
                        blog.getCreatedBy().getUsername() 
                ))
                .toList();

        return ResponseEntity.ok(blogDTOs);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBlog(@PathVariable Long id) {
        blogService.deleteBlog(id);
        return ResponseEntity.noContent().build();
    }
    

    @PostMapping("/creat-blog")
    public ResponseEntity<Response<BlogResponseDTO>> createBlog(@ModelAttribute BlogRequest blogRequest, Authentication authentication) {
        String username = authentication.getName();
        Response<BlogResponseDTO> data = blogService.createBlog(blogRequest, username);
        return ResponseEntity.ok(data);
    }

}