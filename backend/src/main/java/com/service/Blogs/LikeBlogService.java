package com.service.Blogs;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.entity.Notifications;
import com.entity.User;
import com.entity.Blogs.Blog;
import com.entity.Blogs.LikeBlog;
import com.repository.NotificationsRepository;
import com.repository.UserRepository;
import com.repository.Blogs.BlogRepository;
import com.repository.Blogs.LikeBlogRepository;
import com.util.TypeNotifications;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LikeBlogService {

    private final LikeBlogRepository likeBlogRepository;
    private final BlogRepository blogRepository;
    private final UserRepository userRepository;
    private final NotificationsRepository notificationsRepository;

    @Transactional
    public String likeBlog(UUID userId, UUID blogId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if ("BANNED".equals(user.getStatus())) {
            throw new RuntimeException("You are banned from this platform.");
        }

        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new RuntimeException("Blog not found"));

        if (this.likeBlogRepository.existsByUserIdAndBlogId(userId, blogId)) {
            return "Blog already liked";
        }

        if ("hideen".equals(blog.getStatus())) {
            throw new RuntimeException("This blog has been hidden by the admin.");
        }

        LikeBlog likeBlog = new LikeBlog();
        likeBlog.setUser(user);
        likeBlog.setBlog(blog);
        likeBlogRepository.save(likeBlog);

        Notifications notif = new Notifications();
        notif.setCreatorNf(user);
        notif.setIntended_Blog(blog);
        notif.setIntended_User(blog.getCreatedBy());
        notif.setType(TypeNotifications.LIKE);
        notif.setMessage("liked your blog");
        notificationsRepository.save(notif);

        return "liked blog successfully!";
    }

    @Transactional
    public String unLikedBlog(UUID userId, UUID blogId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if ("BANNED".equals(user.getStatus())) {
            throw new RuntimeException("You are banned from this platform.");
        }

        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new RuntimeException("Blog not found"));

        if (!likeBlogRepository.existsByUserIdAndBlogId(userId, blogId)) {
            return "Blog not liked";
        }

        if ("hideen".equals(blog.getStatus())) {
            throw new RuntimeException("This blog has been hidden by the admin.");
        }

        likeBlogRepository.deleteByUserIdAndBlogId(userId, blogId);

        return "UnLiked successfully!";
    }

    public boolean isBlogLiked(UUID userId, UUID blogId) {
        return likeBlogRepository.existsByUserIdAndBlogId(userId, blogId);
    }

    public Long getNumbLike(UUID blogId) {
        return likeBlogRepository.countByBlogId(blogId);
    }
}
