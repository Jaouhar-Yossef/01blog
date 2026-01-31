package com.service.Blogs;

import com.dto.BlogRequest;
import com.dto.BlogResponseDTO;
import com.dto.MediaDTO;
import com.entity.User;
import com.entity.Blogs.Blog;
import com.entity.Blogs.MediaBlog;
import com.repository.UserRepository;
import com.repository.Blogs.BlogRepository;
import com.util.Response;

import jakarta.transaction.Transactional;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class BlogService {

    private final BlogRepository blogRepository;
    private final UserRepository userRepository;    
    private final MediaBlogService mediaBlogService;

    private final SavedService savedService;
    private final LikeBlogService likeBlogService;


    

    public BlogService(BlogRepository blogRepository , UserRepository userRepository,
    LikeBlogService likeBlogService , SavedService savedService,  MediaBlogService mediaBlogService ) {
        this.blogRepository = blogRepository;
        this.userRepository = userRepository;
        this.likeBlogService = likeBlogService;
        this.savedService = savedService;
        this.mediaBlogService = mediaBlogService;
    }



    @Transactional
    public Response<BlogResponseDTO> createBlog(BlogRequest blogRequest, String username) {
        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
                Blog blog = new Blog();
                blog.setTitle(blogRequest.getTitle());
                blog.setStatus("show");
                blog.setContent(blogRequest.getContent());
                blog.setCreatedBy(user);
    
                blogRepository.save(blog);
            
            try {
                mediaBlogService.saveMedia(blog, blogRequest);
            } catch (Exception e) {
                throw new RuntimeException("Error creating blog: " + e.getMessage());
            } 

            return new Response<>(true, "Blog created successfully", null);

        } catch (RuntimeException e) {
            return new Response<>(false, "Error creating blog: " + e.getMessage(), null);
        }
    }


    public List<Blog> getBlogsPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        return blogRepository.findAll(pageable).getContent();
    }


    @Transactional
    public void deleteBlog(UUID id) {
        Blog blog = blogRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Blog not found"));
        for (MediaBlog media : blog.getMedias()) {
            File file = new File("uploads/" + media.getFileName());
            if (file.exists()) {
                file.delete(); 
            }
        }    
        blogRepository.delete(blog);
    }


    public List<BlogResponseDTO> blogsGetter(UUID userId , int page , int size) {

        List<Blog> blogs = this.getBlogsPaginated(page, size);
    
        List<BlogResponseDTO> blogDTOs = blogs.stream()
            .map(blog -> {
                boolean saved = savedService.isBlogSaved(userId, blog.getId());
                boolean liked = this.likeBlogService.isBlogLiked(userId, blog.getId());

                Long numbLike =  this.likeBlogService.getNumbLike(blog.getId());

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
                    liked,
                    numbLike,
                    blog.getCreatedBy().getUsername(),
                    mediaList
                );

            })
            .toList();

        return blogDTOs;    
    }
}
