package com.controller;

import com.dto.BlogRequest;
import com.dto.BlogResponseDTO;
import com.dto.MediaDTO;
import com.entity.Blog;
import com.service.BlogService;

import java.util.List;

import com.util.Response;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security .core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/blogs")
public class BlogController {

    private final BlogService blogService;

    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }



    @GetMapping("/blogs")
    public ResponseEntity<List<BlogResponseDTO>> getBlogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
    
        List<Blog> blogs = blogService.getBlogsPaginated(page, size);
    
        List<BlogResponseDTO> blogDTOs = blogs.stream()
            .map(blog -> {
                List<MediaDTO> mediaList = blog.getMedias().stream()
                                               .map(m -> new MediaDTO(m.getUrl(), m.getFileName(), m.getType()))
                                               .toList();
                return new BlogResponseDTO(
                    blog.getId(),
                    blog.getTitle(),
                    blog.getContent(),
                    blog.getCreatedBy().getUsername(),
                    mediaList
                );
            })
            .toList();
    
        return ResponseEntity.ok(blogDTOs);
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Response<Void>> deleteBlog(@PathVariable Long id) {
        blogService.deleteBlog(id);
        return ResponseEntity.ok(new Response<>(true, "Deleted the blog sucesfuly", null));
    }

    @PostMapping("/creat-blog")
    public ResponseEntity<Response<BlogResponseDTO>> createBlog(
            @Valid @ModelAttribute BlogRequest blogRequest, 
            BindingResult bindingResult,
            Authentication authentication) {
    
        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(new Response<>(false, errorMsg, null));
        }
    
        String username = authentication.getName();
        Response<BlogResponseDTO> data = blogService.createBlog(blogRequest, username);
        return ResponseEntity.created(null).body(data);
    }


}