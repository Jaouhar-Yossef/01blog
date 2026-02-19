package com.service.Blogs;

import com.dto.BlogRequest;
import com.dto.BlogResponseDTO;
import com.dto.MediaDTO;
import com.entity.User;
import com.entity.Blogs.Blog;
import com.entity.Blogs.Saved;
import com.repository.FollowersRepository;
import com.repository.UserRepository;
import com.repository.Blogs.BlogRepository;
import com.repository.Blogs.SavedRepository;
import com.util.Response;

import jakarta.transaction.Transactional;
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
        private final FollowersRepository followersRepository;

        private final SavedService savedService;
        private final LikeBlogService likeBlogService;

        public BlogService(BlogRepository blogRepository, UserRepository userRepository,
                        LikeBlogService likeBlogService, SavedService savedService,
                        MediaBlogService mediaBlogService, SavedRepository savedRepository,
                        FollowersRepository followersRepository) {
                this.blogRepository = blogRepository;
                this.userRepository = userRepository;
                this.likeBlogService = likeBlogService;
                this.savedService = savedService;
                this.mediaBlogService = mediaBlogService;
                this.savedRepository = savedRepository;
                this.followersRepository = followersRepository;
        }

        @Transactional
        public boolean createBlog(BlogRequest blogRequest, String username) throws Exception {

                User user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new RuntimeException("User not found"));
                Blog blog = new Blog();
                blog.setTitle(blogRequest.getTitle());
                blog.setStatus("show");
                blog.setContent(blogRequest.getContent());
                blog.setCreatedBy(user);

                blogRepository.save(blog);

                mediaBlogService.saveMedia(blog, blogRequest, "Creat");
                return true;
        }

        @Transactional
        public boolean upDateBlog(BlogRequest blogRequest, UUID user_id) {
                Blog blog = blogRepository.findById(blogRequest.getIdBlog_update())
                                .orElseThrow(() -> new RuntimeException("Blog not found"));

                if ("hidden".equals(blog.getStatus())) {
                        throw new RuntimeException("This blog has been hidden by the admin.");
                }
                UUID crtBy = blog.getCreatedBy().getId();

                if (!user_id.equals(crtBy)) {
                        throw new RuntimeException("Your not how creat this blog");
                }

                blog.setTitle(blogRequest.getTitle());
                blog.setContent(blogRequest.getContent());

                blogRepository.save(blog);

                mediaBlogService.saveMedia(blog, blogRequest, "update");
                return true;
        }

        @Transactional
        public void deleteBlog(UUID id) {
                Blog blog = blogRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Blog not found"));

                mediaBlogService.deleteBlogFiles(blog);
                blogRepository.delete(blog);
        }

        public Response<?> deleteOneBlog(UUID user_id, UUID blog_id) {
                User user = userRepository.findById(user_id)
                                .orElseThrow(() -> new RuntimeException("User not found"));
                Blog blog = blogRepository.findById(blog_id)
                                .orElseThrow(() -> new RuntimeException("Blog not found"));
                User checking_creat_by = blog.getCreatedBy();

                if (checking_creat_by.getUsername().equals(user.getUsername())) {
                        deleteBlog(blog_id);
                        return new Response<>(true, "Blog deleted sucesfuly!");
                }
                return new Response<>(false, null);
        }

        public List<BlogResponseDTO> blogsGetterHome(UUID userId, int page, int size) {
                Pageable pageable = PageRequest.of(page, size);
                List<Blog> blogs = followersRepository.findBlogsOfFollowedUsers(userId, pageable);

                List<BlogResponseDTO> blogDTOs = blogs.stream()
                                .filter(b -> !"hidden".equals(b.getStatus()))
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
                                                        mediaList,
                                                        blog.getUpdatedAt());

                                })
                                .toList();

                return blogDTOs;
        }

        public List<BlogResponseDTO> blogsGetterSaved(UUID userId, int page, int size) {

                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("User not found"));
                Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
                List<Saved> savedList = savedRepository.findSavedBlogsByUserId(user.getId(), pageable);

                List<Blog> savedBlogs = savedList.stream()
                                .map(Saved::getBlog)
                                .toList();

                List<BlogResponseDTO> blogDTOs = savedBlogs.stream()
                                .filter(b -> !"hidden".equals(b.getStatus()))
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
                                                        mediaList,
                                                        blog.getUpdatedAt());

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
                                                        .map(m -> new MediaDTO(m.getUrl(), m.getFileName(),
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
                                                        mediaList,
                                                        blog.getUpdatedAt());
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
                                mediaList,
                                blog.getUpdatedAt());
        }
}
