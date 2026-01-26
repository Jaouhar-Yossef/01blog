package com.service;

import com.dto.BlogRequest;
import com.dto.BlogResponseDTO;
import com.entity.Blog;
import com.entity.User;
import com.repository.BlogRepository;
import com.repository.UserRepository;
import com.util.Response;

import jakarta.transaction.Transactional;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class BlogService {

    private final BlogRepository blogRepository;
    private final UserRepository userRepository;    
    private final MediaBlogService mediaBlogService;

    public BlogService(BlogRepository blogRepository , UserRepository userRepository, MediaBlogService mediaBlogService ) {
        this.blogRepository = blogRepository;
        this.userRepository = userRepository;
        this.mediaBlogService = mediaBlogService;
    }


    public Response<BlogResponseDTO> createBlog(BlogRequest blogRequest, String username) {
        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
    
            Blog blog = new Blog();
            blog.setTitle(blogRequest.getTitle());
            blog.setContent(blogRequest.getContent());
            blog.setCreatedBy(user);
    
            blogRepository.save(blog);
            mediaBlogService.saveMedia(blog, blogRequest);
    
    
            BlogResponseDTO responseData = new BlogResponseDTO(
                    blog.getTitle(),
                    blog.getContent(),
                    blog.getCreatedBy().getUsername()
            );
    
            return new Response<>(true, "Blog created successfully", responseData);
    
        } catch (RuntimeException e) {
            return new Response<>(false, "Error creating blog: " + e.getMessage(), null);
        }
    }



    public List<Blog> getAllBlogs() {
        return blogRepository.findAll();
    }
    

    @Transactional
    public void deleteBlog(Long id) {
        Blog blog = blogRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Blog not found"));
    
        blogRepository.delete(blog);
    }

}    
