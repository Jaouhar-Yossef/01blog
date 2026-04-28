package com.service.Blogs;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.entity.Blogs.Blog;
import com.entity.Blogs.Saved;
import com.entity.User;
import com.repository.UserRepository;
import com.repository.Blogs.BlogRepository;
import com.repository.Blogs.SavedRepository;
import com.util.BlogStatus;
import com.util.UserStatus;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SavedService {

    private final SavedRepository savedRepository;
    private final BlogRepository blogRepository;
    private final UserRepository userRepository;

    @Transactional
    public void saveBlog(UUID userId, UUID blogId) {

        if (blogId == null) {
            throw new IllegalArgumentException("blog_id cannot be null");
        }

        if (userId == null) {
            throw new IllegalArgumentException("user_id cannot be null");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getStatus() == UserStatus.BANNED) {
            throw new RuntimeException("You are banned from this platform.");
        }

        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new RuntimeException("Blog not found"));
        if (blog.getStatus() == BlogStatus.HIDDEN) {
            throw new RuntimeException("This blog has been hidden by the admin.");
        }

        if (this.savedRepository.existsByUserIdAndBlogId(userId, blogId)) {
            throw new RuntimeException("Blog already saved");
        }

        Saved saved = new Saved();
        saved.setUser(user);
        saved.setBlog(blog);

        savedRepository.save(saved);
    }

    @Transactional
    public void unsaveBlog(UUID userId, UUID blogId) {

        if (blogId == null) {
            throw new IllegalArgumentException("blog_id cannot be null");
        }

        if (userId == null) {
            throw new IllegalArgumentException("user_id cannot be null");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getStatus() == UserStatus.BANNED) {
            throw new RuntimeException("You are banned from this platform.");
        }

        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new RuntimeException("Blog not found"));

        if (blog.getStatus() == BlogStatus.HIDDEN) {
            throw new RuntimeException("This blog has been hidden by the admin.");
        }

        if (!savedRepository.existsByUserIdAndBlogId(userId, blogId)) {
            throw new RuntimeException("Blog not saved");
        }

        savedRepository.deleteByUserIdAndBlogId(userId, blogId);
    }

    @Transactional(readOnly = true)
    public boolean isBlogSaved(UUID userId, UUID blogId) {
        return savedRepository.existsByUserIdAndBlogId(userId, blogId);
    }

    @Transactional(readOnly = true)
    public List<Saved> getSavedBlogs(UUID userId) {
        return savedRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Map<UUID, Boolean> getSavedMap(UUID userId, List<UUID> blogIds) {

        List<UUID> savedIds = savedRepository.findSavedBlogIds(userId, blogIds);

        return savedIds.stream()
                .collect(Collectors.toMap(
                        id -> id,
                        id -> true));
    }
}
