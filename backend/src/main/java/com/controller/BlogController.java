package com.controller;

import com.dto.Request.BlogRequest;
import com.dto.Request.CommentRequestDTO;
import com.dto.Request.LikeOrSaveBlogRequest;
import com.dto.Request.PageableBlogsDTO;
import com.dto.Request.PageableDTO;
import com.dto.Response.BlogResponseDTO;
import com.dto.Response.CommentResponseDTO;
import com.entity.UserDetailsImpl;
import com.service.Blogs.BlogService;
import com.service.Blogs.CommentBlogService;
import com.service.Blogs.LikeBlogService;
import com.service.Blogs.SavedService;
import java.util.List;
import java.util.UUID;

import com.util.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/blogs")
public class BlogController {

    private final BlogService blogService;
    private final SavedService savedService;
    private final LikeBlogService likeBlogService;
    private final CommentBlogService commentBlogService;

    @DeleteMapping("/deleteblog/{blogId}")
    public ResponseEntity<Response<?>> deleteBlog(@PathVariable UUID blogId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (blogId == null) {
            return ResponseEntity.badRequest().body(new Response<>(false, "blog_id cannot be null"));
        }
        try {
            UUID user_id = userDetails.getUser().getId();
            Response<?> blogstatus = blogService.deleteOneBlog(user_id, blogId);
            return ResponseEntity.ok(blogstatus);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(false, e.getMessage()));
        }
    }

    @PostMapping("/like_blog")
    public ResponseEntity<Response<?>> likeBlog(@RequestBody @Valid LikeOrSaveBlogRequest request,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(new Response<>(false, errorMsg));
        }

        try {
            UUID user_id = userDetails.getUser().getId();
            Response<?> data = this.likeBlogService.likeBlog(user_id, request.getId_blog());
            return ResponseEntity.status(HttpStatus.CREATED).body(data);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(false, e.getMessage()));
        }
    }

    @PostMapping("/unlike_blog")
    public ResponseEntity<Response<?>> UnLikeBlog(@RequestBody @Valid LikeOrSaveBlogRequest request,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(new Response<>(false, errorMsg));
        }
        try {
            UUID user_id = userDetails.getUser().getId();
            Response<?> data = this.likeBlogService.unLikedBlog(user_id, request.getId_blog());

            return ResponseEntity.status(HttpStatus.CREATED).body(data);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(false, e.getMessage()));
        }
    }

    @PostMapping("/save_blog")
    public ResponseEntity<Response<?>> saveBlog(@RequestBody @Valid LikeOrSaveBlogRequest request,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(new Response<>(false, errorMsg));
        }
        try {
            UUID user_id = userDetails.getUser().getId();
            this.savedService.saveBlog(user_id, request.getId_blog());
            return ResponseEntity.status(HttpStatus.CREATED).body(new Response<>(true, "saved successfully!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(false, e.getMessage()));
        }

    }

    @PostMapping("/unsave_blog")
    public ResponseEntity<Response<?>> unsaveBlog(@RequestBody @Valid LikeOrSaveBlogRequest request,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(new Response<>(false, errorMsg));
        }

        try {
            UUID user_id = userDetails.getUser().getId();
            this.savedService.unsaveBlog(user_id, request.getId_blog());
            return ResponseEntity.status(HttpStatus.CREATED).body(new Response<>(true, "Unsaved successfully!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(false, e.getMessage()));
        }

    }

    @GetMapping("/blogs")
    public ResponseEntity<Response<List<BlogResponseDTO>>> getBlogs(
            @ModelAttribute @Valid PageableBlogsDTO pageableDTO, BindingResult bindingResult,
            @RequestParam(required = false) String username, @RequestParam(required = false) String search_word,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(new Response<>(false, errorMsg));
        }

        try {
            UUID userId = userDetails.getUser().getId();
            switch (pageableDTO.getMode()) {
                case HOME:
                    List<BlogResponseDTO> blogDTOhome = blogService.blogsGetterHome(userId, pageableDTO.getPage(),
                            pageableDTO.getSize());
                    return ResponseEntity.ok(new Response<>(true, "getBlogs Sucesfuly", blogDTOhome));

                case PROFILE:
                    if (username == null || username.length() == 0) {
                        return ResponseEntity.badRequest()
                                .body(new Response<>(false, "There is no username for the profile"));
                    }
                    List<BlogResponseDTO> blogDTOprofile = blogService.blogsGetterProfile(userId, pageableDTO.getPage(),
                            pageableDTO.getSize(), username);
                    return ResponseEntity.ok(new Response<>(true, "getBlogs Sucesfuly", blogDTOprofile));

                case SAVED:
                  
                    List<BlogResponseDTO> blogDTOsaved = blogService.blogsGetterSaved(userId, pageableDTO.getPage(),
                            pageableDTO.getSize());
                    return ResponseEntity.ok(new Response<>(true, "getBlogs Sucesfuly", blogDTOsaved));
                case SEARCH:
                    if (search_word == null || search_word.length() == 0) {
                        return ResponseEntity.badRequest()
                                .body(new Response<>(false, "There is no search_word for the search"));
                    }
                    List<BlogResponseDTO> blogDTOSearch = blogService.blogsGetterSearch(userId, pageableDTO.getPage(),
                            pageableDTO.getSize(), search_word);
                    return ResponseEntity.ok(new Response<>(true, "getBlogs Sucesfuly", blogDTOSearch));
                default:
                    break;
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(false, e.getMessage()));
        }

        return ResponseEntity.badRequest().body(new Response<>(false, ""));

    }

    @GetMapping("/blog/{id_blog}")
    public ResponseEntity<Response<?>> getBlog(@PathVariable UUID id_blog,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            UUID user_id = userDetails.getUser().getId();
            BlogResponseDTO blogDTOs = blogService.getOneBlog(user_id, id_blog);
            return ResponseEntity.ok(new Response<>(true, null, blogDTOs));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(false, e.getMessage()));
        }
    }

    @GetMapping("/comments/{id_blog}")
    public ResponseEntity<Response<?>> getcomments(@PathVariable UUID id_blog,
            @ModelAttribute @Valid PageableDTO pageableDTO, BindingResult bindingResult,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(new Response<>(false, errorMsg));
        }

        try {
            UUID user_id = userDetails.getUser().getId();
            List<CommentResponseDTO> CommentDTO = this.commentBlogService.getTheComment(user_id, pageableDTO.getPage(),
                    pageableDTO.getSize(), id_blog);
            return ResponseEntity.ok(new Response<>(true, "", CommentDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(false, e.getMessage()));
        }
    }

    @PostMapping("/creat-comment")
    public ResponseEntity<Response<?>> creatcomment(@Valid @RequestBody CommentRequestDTO request,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(new Response<>(false, errorMsg));
        }
        try {
            UUID user_id = userDetails.getUser().getId();
            CommentResponseDTO response = this.commentBlogService.creatComment(user_id, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new Response<CommentResponseDTO>(true, "Comment created sucesfuly!", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(false, e.getMessage()));
        }
    }

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