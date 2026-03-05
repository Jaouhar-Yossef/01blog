package com.service.Blogs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.entity.Notifications;
import com.entity.User;
import com.entity.Blogs.Blog;
import com.entity.Blogs.LikeBlog;
import com.repository.NotificationsRepository;
import com.repository.UserRepository;
import com.repository.Blogs.BlogRepository;
import com.repository.Blogs.LikeBlogRepository;
import com.util.BlogStatus;
import com.util.Response;
import com.util.TypeNotifications;
import com.util.UserStatus;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Transactional
@Service
@RequiredArgsConstructor
public class LikeBlogService {

    private final LikeBlogRepository likeBlogRepository;
    private final BlogRepository blogRepository;
    private final UserRepository userRepository;
    private final NotificationsRepository notificationsRepository;

    public Response<?> likeBlog(UUID userId, UUID blogId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getStatus() == UserStatus.BANNED) {
            throw new RuntimeException("You are banned from this platform.");
        }

        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new RuntimeException("Blog not found"));

        if (this.likeBlogRepository.existsByUserIdAndBlogId(userId, blogId)) {
            throw new RuntimeException("Blog already liked");
        }

        if (blog.getStatus() == BlogStatus.HIDDEN) {
            throw new RuntimeException("This blog has been hidden by the admin.");
        }

        LikeBlog likeBlog = new LikeBlog();
        likeBlog.setUser(user);
        likeBlog.setBlog(blog);
        likeBlogRepository.save(likeBlog);

        if (userId.equals(blog.getCreatedBy().getId())) {
            return new Response<>(true, "liked blog successfully!");
        }

        List<Notifications> existing = notificationsRepository.findNotification(
                TypeNotifications.LIKE,
                blog,
                blog.getCreatedBy(),
                user);
        if (existing.isEmpty()) {
            Notifications notif = new Notifications();
            notif.setActive(true);
            notif.setCreatorNf(user);
            notif.setIntendedBlog(blog);
            notif.setIntendedUser(blog.getCreatedBy());
            notif.setType(TypeNotifications.LIKE);
            notif.setMessage("liked your blog");
            notificationsRepository.save(notif);
        }
        return new Response<>(true, "liked blog successfully!");
    }

    public Response<?> unLikedBlog(UUID userId, UUID blogId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getStatus() == UserStatus.BANNED) {
            throw new RuntimeException("You are banned from this platform.");
        }

        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new RuntimeException("Blog not found"));

        if (!likeBlogRepository.existsByUserIdAndBlogId(userId, blogId)) {
            throw new RuntimeException("Blog already not liked");
        }

        if (blog.getStatus() == BlogStatus.HIDDEN) {
            throw new RuntimeException("This blog has been hidden by the admin.");
        }

        List<Notifications> existing = notificationsRepository.findNotification(
                TypeNotifications.LIKE,
                blog,
                blog.getCreatedBy(),
                user);

        notificationsRepository.deleteAll(existing);

        likeBlogRepository.deleteByUserIdAndBlogId(userId, blogId);

        return new Response<>(true, "UnLiked successfully!");
    }

    public boolean isBlogLiked(UUID userId, UUID blogId) {
        return likeBlogRepository.existsByUserIdAndBlogId(userId, blogId);
    }

    public Long getNumbLike(UUID blogId) {
        return likeBlogRepository.countByBlogId(blogId);
    }

    public Map<UUID, Long> getLikeCountMap(List<UUID> blogIds) {

        List<Object[]> results = likeBlogRepository.countLikesByBlogIds(blogIds);
        Map<UUID, Long> map = new HashMap<>();
        for (Object[] row : results) {
            UUID blogId = (UUID) row[0];
            Long count = (Long) row[1];
            map.put(blogId, count);
        }

        return map;
    }

    public Map<UUID, Boolean> getLikedMap(UUID userId, List<UUID> blogIds) {

        List<UUID> likedIds = likeBlogRepository.findLikedBlogIds(userId, blogIds);
        return likedIds.stream()
                .collect(Collectors.toMap(
                        id -> id,
                        id -> true));
    }
}
