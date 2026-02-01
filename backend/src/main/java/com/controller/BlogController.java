package com.controller;

import com.dto.BlogRequest;
import com.dto.BlogResponseDTO;
import com.dto.CommentRequestDTO;
import com.dto.CommentResponseDTO;
import com.dto.LikeOrSaveBlogRequest;
import com.entity.UserDetailsImpl;
import com.service.Blogs.BlogService;
import com.service.Blogs.CommentBlogService;
import com.service.Blogs.LikeBlogService;
import com.service.Blogs.SavedService;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.util.Response;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    private final LikeBlogService likeBlogService;
    private final CommentBlogService commentBlogService;
    

    public BlogController(BlogService blogService  ,CommentBlogService commentBlogService , LikeBlogService likeBlogService,SavedService savedService) {
        this.blogService = blogService;
        this.likeBlogService = likeBlogService;
        this.savedService = savedService;
        this.commentBlogService = commentBlogService;
    }


    @PostMapping("/creat-comment")
    public ResponseEntity<Response<?>> creatcomment(@RequestBody CommentRequestDTO request, 
            @AuthenticationPrincipal UserDetailsImpl userDetails
            ) {
       
        try {        
            UUID user_id = userDetails.getUser().getId();
            CommentResponseDTO response = this.commentBlogService.creatComment(user_id, request);
            return ResponseEntity.status(HttpStatus. CREATED).body(new Response<CommentResponseDTO>(true, "Comment created sucesfuly!",response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(false, e.getMessage()));
        } 
    }



    // @PostMapping("/the_comment")
    // public ResponseEntity<Response<?>> creatcomment(@RequestBody CommentRequestDTO request, 
    //         @AuthenticationPrincipal UserDetailsImpl userDetails
    //         ) {
       
    //     try {        
    //         UUID user_id = userDetails.getUser().getId();
    //         CommentResponseDTO response = this.commentBlogService.creatComment(user_id, request);
    //         return ResponseEntity.status(HttpStatus. CREATED).body(new Response<CommentResponseDTO>(false, "Comment created sucesfuly!",response));
    //     } catch (Exception e) {
    //         return ResponseEntity.badRequest().body(new Response<>(false, e.getMessage()));
    //     } 
    // }

    @PostMapping("/like_blog")
    public ResponseEntity<?> likeBlog(@RequestBody LikeOrSaveBlogRequest request, 
            @AuthenticationPrincipal UserDetailsImpl userDetails
            ) {
        UUID user_id = userDetails.getUser().getId();
        String msg = this.likeBlogService.likeBlog(user_id, request.getId_blog());        
        return ResponseEntity.ok(Map.of("message", msg));        
    }

    @PostMapping("/unlike_blog")
    public ResponseEntity<?> UnLikeBlog(@RequestBody LikeOrSaveBlogRequest request, 
            @AuthenticationPrincipal UserDetailsImpl userDetails
            ) {

        UUID user_id = userDetails.getUser().getId();
        String msg = this.likeBlogService.unLikedBlog(user_id, request.getId_blog());        

        return ResponseEntity.ok(Map.of("message", msg));
    }


    @PostMapping("/save_blog")
    public ResponseEntity<?> saveBlog( @RequestBody LikeOrSaveBlogRequest request, 
            @AuthenticationPrincipal UserDetailsImpl userDetails
            ) {

        UUID user_id = userDetails.getUser().getId();
        String msg = this.savedService.saveBlog(user_id, request.getId_blog());

        return ResponseEntity.ok(Map.of("message", msg));
    }



    @PostMapping("/unsave_blog")
    public ResponseEntity<?> unsaveBlog( @RequestBody LikeOrSaveBlogRequest request, 
            @AuthenticationPrincipal UserDetailsImpl userDetails
            ) {

        UUID user_id = userDetails.getUser().getId();
        String msg = this.savedService.unsaveBlog(user_id, request.getId_blog());

        return ResponseEntity.ok(Map.of("message", msg));
    }

    @GetMapping("/blogs")
    public ResponseEntity<List<BlogResponseDTO>> getBlogs( @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size, @AuthenticationPrincipal UserDetailsImpl userDetails 
            ) {

        UUID userId = userDetails.getUser().getId();
        List<BlogResponseDTO> blogDTOs = blogService.blogsGetter(userId , page , size);
        return ResponseEntity.ok(blogDTOs);
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Response<Void>> deleteBlog(@PathVariable UUID id) {
        blogService.deleteBlog(id);
        return ResponseEntity.ok(new Response<>(true, "Deleted the blog sucesfuly", null));
    }

    @PostMapping(value = "/creat-blog", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Response<BlogResponseDTO>> create_Blog(
            @Valid @ModelAttribute BlogRequest blogRequest, 
            BindingResult bindingResult,
            Authentication authentication) {
    
        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(new Response<>(false, errorMsg, null));
        }

        String username = authentication.getName();

        try {
            Response<BlogResponseDTO> dataa = blogService.createBlog(blogRequest, username);
            return ResponseEntity.status(HttpStatus.CREATED).body(dataa);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(false, e.getMessage()));
        }
    }
}