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

    public BlogController(BlogService blogService, CommentBlogService commentBlogService,
            LikeBlogService likeBlogService, SavedService savedService) {
        this.blogService = blogService;
        this.likeBlogService = likeBlogService;
        this.savedService = savedService;
        this.commentBlogService = commentBlogService;
    }

    @DeleteMapping("/deleteblog/{blogId}")
    public ResponseEntity<Response<?>> deleteBlog(@PathVariable UUID blogId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            UUID user_id = userDetails.getUser().getId();
            Response<?> blogstatus = blogService.deleteOneBlog(user_id, blogId);
            return ResponseEntity.ok(blogstatus);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(false, e.getMessage()));
        }
    }

    @PostMapping("/creat-comment")
    public ResponseEntity<Response<?>> creatcomment(@RequestBody CommentRequestDTO request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        try {
            UUID user_id = userDetails.getUser().getId();
            CommentResponseDTO response = this.commentBlogService.creatComment(user_id, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new Response<CommentResponseDTO>(true, "Comment created sucesfuly!", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(false, e.getMessage()));
        }
    }

    @PostMapping("/like_blog")
    public ResponseEntity<?> likeBlog(@RequestBody LikeOrSaveBlogRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        UUID user_id = userDetails.getUser().getId();
        String msg = this.likeBlogService.likeBlog(user_id, request.getId_blog());
        return ResponseEntity.ok(Map.of("message", msg));
    }

    @PostMapping("/unlike_blog")
    public ResponseEntity<?> UnLikeBlog(@RequestBody LikeOrSaveBlogRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        UUID user_id = userDetails.getUser().getId();
        String msg = this.likeBlogService.unLikedBlog(user_id, request.getId_blog());

        return ResponseEntity.ok(Map.of("message", msg));
    }

    @PostMapping("/save_blog")
    public ResponseEntity<Response<?>> saveBlog(@RequestBody LikeOrSaveBlogRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        UUID user_id = userDetails.getUser().getId();
        try {
            this.savedService.saveBlog(user_id, request.getId_blog());
            return ResponseEntity.status(HttpStatus.CREATED).body(new Response<>(true, "saved successfully!"));            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(false, e.getMessage()));
        }

    }

    @PostMapping("/unsave_blog")
    public ResponseEntity<Response<?>> unsaveBlog(@RequestBody LikeOrSaveBlogRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        UUID user_id = userDetails.getUser().getId();

        try {
            this.savedService.unsaveBlog(user_id, request.getId_blog());
            return ResponseEntity.status(HttpStatus.CREATED).body(new Response<>(true, "Unsaved successfully!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(false, e.getMessage()));
        }

    }

    @GetMapping("/blogs")
    public ResponseEntity<Response<List<BlogResponseDTO>>> getBlogs(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam String mode,
            @RequestParam(required = false) String username, @AuthenticationPrincipal UserDetailsImpl userDetails) {

        try {
            UUID userId = userDetails.getUser().getId();

            if ("home".equals(mode)) {
                List<BlogResponseDTO> blogDTOs = blogService.blogsGetterHome(userId, page, size);
                return ResponseEntity.ok(new Response<>(true, "getBlogs Sucesfuly", blogDTOs));
            } else if ("profile".equals(mode)) {
                List<BlogResponseDTO> blogDTOs = blogService.blogsGetterProfile(userId, page, size, username);
                return ResponseEntity.ok(new Response<>(true, "getBlogs Sucesfuly", blogDTOs));
            } else if ("saved".equals(mode)) {
                List<BlogResponseDTO> blogDTOs = blogService.blogsGetterSaved(userId, page, size);
                return ResponseEntity.ok(new Response<>(true, "getBlogs Sucesfuly", blogDTOs));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(false, e.getMessage()));
        }
        return ResponseEntity.badRequest().body(new Response<>(false, "Bad Request"));
    }

    @GetMapping("/blog/{id_blog}")
    public ResponseEntity<?> getBlog(@PathVariable UUID id_blog,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        try {
            UUID user_id = userDetails.getUser().getId();
            BlogResponseDTO blogDTOs = blogService.getOneBlog(user_id, id_blog);
            return ResponseEntity.ok(blogDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Blog not found or error occurred");
        }
    }

    @GetMapping("/comments/{id_blog}")
    public ResponseEntity<Response<?>> getcomments(@PathVariable UUID id_blog,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        UUID user_id = userDetails.getUser().getId();

        try {
            List<CommentResponseDTO> CommentDTO = this.commentBlogService.getTheComment(user_id, page, size, id_blog);

            return ResponseEntity.ok(new Response<>(true, "", CommentDTO));

        } catch (Exception e) {

            return ResponseEntity.badRequest().body(new Response<>(false, e.getMessage()));
        }

    }

    // @DeleteMapping("/{id}")
    // public ResponseEntity<Response<Void>> deleteBlog(@PathVariable UUID id) {
    // blogService.deleteBlog(id);
    // return ResponseEntity.ok(new Response<>(true, "Deleted the blog sucesfuly!",
    // null));
    // }

    @PostMapping(value = "/creat-blog", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Response<?>> create_Blog(
            @Valid @ModelAttribute BlogRequest blogRequest,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(new Response<>(false, errorMsg));
        }

        String username = userDetails.getUser().getUsername();

        try {
            Response<?> dataa = blogService.createBlog(blogRequest, username);
            return ResponseEntity.status(HttpStatus.CREATED).body(dataa);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(false, e.getMessage()));
        }
    }

    @PutMapping(value = "/update-blog", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Response<?>> update_blog(
            @Valid @ModelAttribute BlogRequest blogRequest,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(new Response<>(false, errorMsg));
        }

        UUID user_id = userDetails.getUser().getId();

        try {
            Response<?> data = blogService.upDateBlog(blogRequest, user_id);
            return ResponseEntity.status(HttpStatus.CREATED).body(data);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(false, e.getMessage()));
        }
    }
}