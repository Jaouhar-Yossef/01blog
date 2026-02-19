package com.service.Blogs;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.entity.User;
import com.entity.Blogs.Blog;
import com.entity.Blogs.LikeBlog;
import com.entity.Blogs.Saved;
import com.repository.UserRepository;
import com.repository.Blogs.BlogRepository;
import com.repository.Blogs.LikeBlogRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LikeBlogService {

    private final LikeBlogRepository likeBlogRepository;
    private final BlogRepository blogRepository;
    private final UserRepository userRepository;

    public String likeBlog(UUID userId, UUID blogId) {

        if (this.likeBlogRepository.existsByUserIdAndBlogId(userId, blogId)) {
            return "Blog already liked";
        }

        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new RuntimeException("Blog not found"));

        if ("hideen".equals(blog.getStatus())) {
            new RuntimeException("This blog has been hidden by the admin.");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LikeBlog likeBlog = new LikeBlog();
        likeBlog.setUser(user);
        likeBlog.setBlog(blog);
        likeBlogRepository.save(likeBlog);
        return "liked blog successfully!";
    }

    @Transactional
    public String unLikedBlog(UUID userId, UUID blogId) {
        if (!likeBlogRepository.existsByUserIdAndBlogId(userId, blogId)) {
            return "Blog not liked";
        }
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new RuntimeException("Blog not found"));

        if ("hideen".equals(blog.getStatus())) {
            new RuntimeException("This blog has been hidden by the admin.");
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

    public List<Saved> getLikeBlogs(UUID userId) {
        return likeBlogRepository.findByUserId(userId);
    }

}
