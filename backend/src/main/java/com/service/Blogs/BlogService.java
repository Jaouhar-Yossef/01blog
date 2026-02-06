package com.service.Blogs;

import com.dto.BlogRequest;
import com.dto.BlogResponseDTO;
import com.dto.MediaDTO;
import com.entity.User;
import com.entity.Blogs.Blog;
import com.entity.Blogs.MediaBlog;
import com.entity.Blogs.Saved;
import com.repository.UserRepository;
import com.repository.Blogs.BlogRepository;
import com.repository.Blogs.SavedRepository;
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
    private final SavedRepository savedRepository;

    private final SavedService savedService;
    private final LikeBlogService likeBlogService;

    public BlogService(BlogRepository blogRepository, UserRepository userRepository,
            LikeBlogService likeBlogService, SavedService savedService,
             MediaBlogService mediaBlogService , SavedRepository savedRepository) {
        this.blogRepository = blogRepository;
        this.userRepository = userRepository;
        this.likeBlogService = likeBlogService;
        this.savedService = savedService;
        this.mediaBlogService = mediaBlogService;
        this.savedRepository = savedRepository;
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

    private List<Blog> getBlogsPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        return blogRepository.findAll(pageable).getContent();
    }

  
    public List<BlogResponseDTO> blogsGetterHome(UUID userId, int page, int size) {

        List<Blog> blogs = this.getBlogsPaginated(page, size);

        List<BlogResponseDTO> blogDTOs = blogs.stream()
                .map(blog -> {
                    boolean saved = savedService.isBlogSaved(userId, blog.getId());
                    boolean liked = this.likeBlogService.isBlogLiked(userId, blog.getId());

                    Long numbLike = this.likeBlogService.getNumbLike(blog.getId());

                    List<MediaDTO> mediaList = blog.getMedias().stream()
                            .map(m -> new MediaDTO(
                                    m.getUrl(),
                                    m.getFileName(),
                                    m.getType()))
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
                            blog.getCreatedBy().getImageUrl(),
                            mediaList);

                })
                .toList();

        return blogDTOs;
    }

    public List<BlogResponseDTO> blogsGetterSaved (UUID userId, int page, int size) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Pageable pageable = PageRequest.of(page, size);
        List<Saved> savedList = savedRepository.findSavedBlogsByUserId(user.getId(), pageable);

        List<Blog> savedBlogs = savedList.stream()
                .map(Saved::getBlog)
                .toList();
                
        List<BlogResponseDTO> blogDTOs = savedBlogs.stream()
                .map(blog -> {
                    boolean saved = true;
                    boolean liked = this.likeBlogService.isBlogLiked(user.getId(), blog.getId());

                    Long numbLike = this.likeBlogService.getNumbLike(blog.getId());

                    List<MediaDTO> mediaList = blog.getMedias().stream()
                            .map(m -> new MediaDTO(
                                    m.getUrl(),
                                    m.getFileName(),
                                    m.getType()))
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
                            blog.getCreatedBy().getImageUrl(),
                            mediaList);

                })
                .toList();

        return blogDTOs;
    }

    public List<BlogResponseDTO> blogsGetterProfile(
            UUID userId,
            int page,
            int size,
            String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        List<Blog> blogs = blogRepository.findByCreatedById(user.getId(), pageable);

        List<BlogResponseDTO> blogDTOs = blogs.stream()
                .map(blog -> {
                    boolean saved = savedService.isBlogSaved(userId, blog.getId());
                    boolean liked = likeBlogService.isBlogLiked(userId, blog.getId());
                    Long numbLike = likeBlogService.getNumbLike(blog.getId());

                    List<MediaDTO> mediaList = blog.getMedias().stream()
                            .map(m -> new MediaDTO(m.getUrl(), m.getFileName(), m.getType()))
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
                            blog.getCreatedBy().getImageUrl(),
                            mediaList);
                })
                .toList();

        return blogDTOs;
    }

    public BlogResponseDTO getOneBlog(UUID user_id, UUID id_blog) {
        Blog blog = blogRepository.findById(id_blog)
                .orElseThrow(() -> new RuntimeException("Blog not found"));

        userRepository.findById(user_id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean saved = savedService.isBlogSaved(user_id, blog.getId());
        boolean liked = this.likeBlogService.isBlogLiked(user_id, blog.getId());

        Long numbLike = this.likeBlogService.getNumbLike(blog.getId());

        List<MediaDTO> mediaList = blog.getMedias().stream()
                .map(m -> new MediaDTO(
                        m.getUrl(),
                        m.getFileName(),
                        m.getType()))
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
                blog.getCreatedBy().getImageUrl(),
                mediaList);
    }
}
