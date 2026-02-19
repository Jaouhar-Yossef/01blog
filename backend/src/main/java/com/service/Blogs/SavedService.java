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

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SavedService {

    private final SavedRepository savedRepository;
    private final BlogRepository blogRepository;
    private final UserRepository userRepository;

    public String saveBlog(UUID userId, UUID blogId) {

        if (this.savedRepository.existsByUserIdAndBlogId(userId, blogId)) {
            return "Blog already saved";
        }

        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new RuntimeException("Blog not found"));
        if ("hideen".equals(blog.getStatus())) {
            new RuntimeException("This blog has been hidden by the admin.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Saved saved = new Saved();
        saved.setUser(user);
        saved.setBlog(blog);

        savedRepository.save(saved);

        return "Saved successfully!";
    }

    @Transactional
    public String unsaveBlog(UUID userId, UUID blogId) {

        if (!savedRepository.existsByUserIdAndBlogId(userId, blogId)) {
            return "Blog not saved";
        }

        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new RuntimeException("Blog not found"));
                
        if ("hideen".equals(blog.getStatus())) {
            new RuntimeException("This blog has been hidden by the admin.");
        }

        savedRepository.deleteByUserIdAndBlogId(userId, blogId);

        return "Unsaved successfully!";
    }

    public boolean isBlogSaved(UUID userId, UUID blogId) {
        return savedRepository.existsByUserIdAndBlogId(userId, blogId);
    }

    public List<Saved> getSavedBlogs(UUID userId) {
        return savedRepository.findByUserId(userId);
    }
}
