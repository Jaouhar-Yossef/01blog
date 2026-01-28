package com.service.Blogs;

import com.dto.BlogRequest;
import com.dto.BlogResponseDTO;
import com.dto.MediaDTO;
import com.entity.User;
import com.entity.Blogs.Blog;
import com.repository.UserRepository;
import com.repository.Blogs.BlogRepository;
import com.util.Response;

import jakarta.transaction.Transactional;

import java.util.List;

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
            mediaBlogService.saveMedia(blog, blogRequest);

            List<MediaDTO> mediaList = blog.getMedias().stream()
                                           .map(m -> new MediaDTO(m.getUrl(), m.getFileName(), m.getType()))
                                           .toList();

            boolean saved = savedService.isBlogSaved(user.getId() , blog.getId());
            boolean liked = this.likeBlogService.isBlogLiked(user.getId(), blog.getId());
            Long numbLike =  this.likeBlogService.getNumbLike(blog.getId());
                               
            BlogResponseDTO responseData = new BlogResponseDTO(
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

            return new Response<>(true, "Blog created successfully", responseData);

        } catch (RuntimeException e) {
            return new Response<>(false, "Error creating blog: " + e.getMessage(), null);
        }
    }


    public List<Blog> getBlogsPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return blogRepository.findAll(pageable).getContent();
    }


    @Transactional
    public void deleteBlog(Long id) {
        Blog blog = blogRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Blog not found"));
    
        blogRepository.delete(blog);
    }
}
