package com.service.Blogs;

import com.dto.Request.BlogRequest;
import com.dto.Response.BlogResponseDTO;
import com.dto.Response.MediaDTO;
import com.entity.User;
import com.entity.Blogs.Blog;
import com.entity.Blogs.Saved;
import com.repository.FollowersRepository;
import com.repository.UserRepository;
import com.repository.Blogs.BlogRepository;
import com.repository.Blogs.SavedRepository;
import com.util.BlogStatus;
import com.util.Response;
import com.util.UserStatus;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Transactional
@RequiredArgsConstructor
@Service
public class BlogService {

        private final BlogRepository blogRepository;
        private final UserRepository userRepository;
        private final MediaBlogService mediaBlogService;
        private final SavedRepository savedRepository;
        private final FollowersRepository followersRepository;
        private final SavedService savedService;
        private final LikeBlogService likeBlogService;

        @Transactional
        public Response<?> createBlog(BlogRequest blogRequest, String username) throws Exception {

                User user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new RuntimeException("User not found"));
                if (user.getStatus() == UserStatus.BANNED) {
                        return new Response<>(false, "You are banned from this platform.");
                }
                Blog blog = new Blog();
                blog.setTitle(blogRequest.getTitle());
                blog.setStatus(BlogStatus.SHOW);
                blog.setContent(blogRequest.getContent());
                blog.setCreatedBy(user);

                blogRepository.save(blog);

                mediaBlogService.saveMedia(blog, blogRequest, "Creat");
                return new Response<>(true, "Blog created successfully!");
        }

        @Transactional
        public Response<?> upDateBlog(BlogRequest blogRequest, UUID user_id) {

                User user = userRepository.findById(user_id)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                if (user.getStatus() == UserStatus.BANNED) {
                        return new Response<>(false, "You are banned from this platform.");
                }

                Blog blog = blogRepository.findById(blogRequest.getIdBlog_update())
                                .orElseThrow(() -> new RuntimeException("Blog not found"));

                if (blog.getStatus() == BlogStatus.HIDDEN) {
                        throw new RuntimeException("This blog has been hidden by the admin.");
                }

                UUID crtBy = blog.getCreatedBy().getId();

                if (!user_id.equals(crtBy)) {
                        throw new RuntimeException("Your not how creat this blog");
                }

                blog.setTitle(blogRequest.getTitle());
                blog.setContent(blogRequest.getContent());

                mediaBlogService.saveMedia(blog, blogRequest, "update");
                return new Response<>(true, "Blog upDated successfully!");
        }

        @Transactional
        public Response<?> deleteOneBlog(UUID user_id, UUID blog_id) {
                User user = userRepository.findById(user_id)
                                .orElseThrow(() -> new RuntimeException("User not found"));
                if (user.getStatus() == UserStatus.BANNED) {
                        return new Response<>(false, "You are banned from this platform.");
                }
                Blog blog = blogRepository.findById(blog_id)
                                .orElseThrow(() -> new RuntimeException("Blog not found"));

                User checking_creat_by = blog.getCreatedBy();

                if (checking_creat_by.getUsername().equals(user.getUsername())
                                || user.getStatus() == UserStatus.ADMIN) {
                        mediaBlogService.deleteBlogFiles(blog);
                        blogRepository.delete(blog);
                        return new Response<>(true, "Blog deleted sucesfuly!");
                }
                return new Response<>(false, null);
        }

        @Transactional(readOnly = true)
        public List<BlogResponseDTO> blogsGetterHome(UUID userId, int page, int size) {
                Pageable pageable = PageRequest.of(page, size);
                List<Blog> blogs = followersRepository.findBlogsOfFollowedUsers(userId, pageable);

                List<UUID> blogIds = blogs.stream()
                                .map(Blog::getId)
                                .toList();

                Map<UUID, Boolean> savedMap = savedService.getSavedMap(userId, blogIds);
                Map<UUID, Boolean> likedMap = likeBlogService.getLikedMap(userId, blogIds);
                Map<UUID, Long> likeCountMap = likeBlogService.getLikeCountMap(blogIds);

                List<BlogResponseDTO> blogDTOs = blogs.stream()
                                .filter(b -> b.getStatus() != BlogStatus.HIDDEN)
                                .map(blog -> {

                                        boolean saved = savedMap.getOrDefault(blog.getId(), false);
                                        boolean liked = likedMap.getOrDefault(blog.getId(), false);
                                        Long numbLike = likeCountMap.getOrDefault(blog.getId(), 0L);

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

        @Transactional(readOnly = true)
        public List<BlogResponseDTO> blogsGetterSaved(UUID userId, int page, int size) {

                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("User not found"));
                Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
                List<Saved> savedList = savedRepository.findSavedBlogsByUserId(user.getId(), pageable);

                List<Blog> savedBlogs = savedList.stream()
                                .map(Saved::getBlog)
                                .toList();

                List<UUID> blogIds = savedBlogs.stream()
                                .map(Blog::getId)
                                .toList();

                Map<UUID, Boolean> savedMap = savedService.getSavedMap(userId, blogIds);
                Map<UUID, Boolean> likedMap = likeBlogService.getLikedMap(userId, blogIds);
                Map<UUID, Long> likeCountMap = likeBlogService.getLikeCountMap(blogIds);

                List<BlogResponseDTO> blogDTOs = savedBlogs.stream()
                                .filter(b -> b.getStatus() != BlogStatus.HIDDEN)
                                .map(blog -> {
                                        boolean saved = savedMap.getOrDefault(blog.getId(), false);
                                        boolean liked = likedMap.getOrDefault(blog.getId(), false);
                                        Long numbLike = likeCountMap.getOrDefault(blog.getId(), 0L);

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

        @Transactional(readOnly = true)
        public List<BlogResponseDTO> blogsGetterProfile(
                        UUID userId,
                        int page,
                        int size,
                        String username) {
                if (!userRepository.existsById(userId)) {
                        throw new RuntimeException("User not found");
                }
                User user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));

                List<Blog> blogs = blogRepository.findByCreatedById(user.getId(), pageable);

                List<UUID> blogIds = blogs.stream()
                                .map(Blog::getId)
                                .toList();

                Map<UUID, Boolean> savedMap = savedService.getSavedMap(userId, blogIds);
                Map<UUID, Boolean> likedMap = likeBlogService.getLikedMap(userId, blogIds);
                Map<UUID, Long> likeCountMap = likeBlogService.getLikeCountMap(blogIds);

                List<BlogResponseDTO> blogDTOs = blogs.stream()
                                .filter(b -> b.getCreatedBy().getId().equals(userId)
                                                || !b.getStatus().equals(BlogStatus.HIDDEN))
                                .map(blog -> {
                                        boolean saved = savedMap.getOrDefault(blog.getId(), false);
                                        boolean liked = likedMap.getOrDefault(blog.getId(), false);
                                        Long numbLike = likeCountMap.getOrDefault(blog.getId(), 0L);

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

        @Transactional(readOnly = true)
        public List<BlogResponseDTO> blogsGetterSearch(
                        UUID userId,
                        int page,
                        int size,
                        String search_word) {
                if (!userRepository.existsById(userId)) {
                        throw new RuntimeException("User not found");
                }

                Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));

                Page<Blog> blogs = blogRepository
                                .findByTitleContainingIgnoreCase(search_word, pageable);
                                
                List<UUID> blogIds = blogs.stream()
                                .map(Blog::getId)
                                .toList();

                Map<UUID, Boolean> savedMap = savedService.getSavedMap(userId, blogIds);
                Map<UUID, Boolean> likedMap = likeBlogService.getLikedMap(userId, blogIds);
                Map<UUID, Long> likeCountMap = likeBlogService.getLikeCountMap(blogIds);

                List<BlogResponseDTO> blogDTOs = blogs.stream()
                                .filter(b -> b.getCreatedBy().getId().equals(userId)
                                                || !b.getStatus().equals(BlogStatus.HIDDEN))
                                .map(blog -> {
                                        boolean saved = savedMap.getOrDefault(blog.getId(), false);
                                        boolean liked = likedMap.getOrDefault(blog.getId(), false);
                                        Long numbLike = likeCountMap.getOrDefault(blog.getId(), 0L);

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

        @Transactional(readOnly = true)
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
