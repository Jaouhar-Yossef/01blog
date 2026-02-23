package com.service.Blogs;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.entity.Blogs.Blog;
import com.entity.Blogs.Saved;
import com.entity.User;
import com.repository.UserRepository;
import com.repository.Blogs.BlogRepository;
import com.repository.Blogs.SavedRepository;
import com.util.UserStatus;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class SavedService {

    private final SavedRepository savedRepository;
    private final BlogRepository blogRepository;
    private final UserRepository userRepository;

    public void saveBlog(UUID userId, UUID blogId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getStatus() == UserStatus.BANNED) {
            throw new RuntimeException("You are banned from this platform."); 
        }

        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new RuntimeException("Blog not found"));
        if ("hideen".equals(blog.getStatus())) {
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

    public void unsaveBlog(UUID userId, UUID blogId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getStatus() == UserStatus.BANNED) {
            throw new RuntimeException("You are banned from this platform.");
        }

        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new RuntimeException("Blog not found"));

        if ("hideen".equals(blog.getStatus())) {
            throw new RuntimeException("This blog has been hidden by the admin.");
        }

        if (!savedRepository.existsByUserIdAndBlogId(userId, blogId)) {
            throw new RuntimeException("Blog not saved");
        }

        savedRepository.deleteByUserIdAndBlogId(userId, blogId);
    }

    public boolean isBlogSaved(UUID userId, UUID blogId) {
        return savedRepository.existsByUserIdAndBlogId(userId, blogId);
    }

    public List<Saved> getSavedBlogs(UUID userId) {
        return savedRepository.findByUserId(userId);
    }
}
