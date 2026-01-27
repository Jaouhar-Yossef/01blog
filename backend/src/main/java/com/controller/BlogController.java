package com.controller;

import com.dto.BlogRequest;
import com.dto.BlogResponseDTO;
import com.dto.MediaDTO;
import com.dto.SaveBlogRequest;
import com.entity.UserDetailsImpl;
import com.entity.Blogs.Blog;
import com.repository.Blogs.SavedRepository;
import com.service.Blogs.BlogService;
import com.service.Blogs.SavedService;

import java.util.List;
import java.util.Map;

import com.util.Response;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security .core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/blogs")
public class BlogController {

    private final BlogService blogService;
    private final SavedService savedService;
    

    public BlogController(BlogService blogService , SavedService savedService) {
        this.blogService = blogService;
        this.savedService = savedService;
    }


    @PostMapping("/save_blog")
    public ResponseEntity<?> saveBlog( @RequestBody SaveBlogRequest request, 
            @AuthenticationPrincipal UserDetailsImpl userDetails
            ) {

        Long user_id = userDetails.getUser().getId();
        String msg = this.savedService.saveBlog(user_id, request.getId_blog());

        return ResponseEntity.ok(Map.of("message", msg));
    }



    @PostMapping("/unsave_blog")
    public ResponseEntity<?> unsaveBlog( @RequestBody SaveBlogRequest request, 
            @AuthenticationPrincipal UserDetailsImpl userDetails
            ) {

        Long user_id = userDetails.getUser().getId();
        String msg = this.savedService.unsaveBlog(user_id, request.getId_blog());

        return ResponseEntity.ok(Map.of("message", msg));
    }

    @GetMapping("/blogs")
    public ResponseEntity<List<BlogResponseDTO>> getBlogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
    
        Long userId = userDetails.getUser().getId();
    
        List<Blog> blogs = blogService.getBlogsPaginated(page, size);
    
        List<BlogResponseDTO> blogDTOs = blogs.stream()
            .map(blog -> {
    
    
                boolean saved = savedService.isBlogSaved(userId, blog.getId());
    
                List<MediaDTO> mediaList = blog.getMedias().stream()
                    .map(m -> new MediaDTO(
                            m.getUrl(),
                            m.getFileName(),
                            m.getType()
                    ))
                    .toList();
    
                return new BlogResponseDTO(
                    blog.getId(),
                    blog.getTitle(),
                    blog.getStatus(),
                    blog.getContent(),
                    saved,
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
    public ResponseEntity<Response<BlogResponseDTO>> create_Blog(
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