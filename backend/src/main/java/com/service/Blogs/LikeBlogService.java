package com.service.Blogs;

import java.util.List;

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

    public String likeBlog(Long userId, Long blogId) {

        if (this.likeBlogRepository.existsByUserIdAndBlogId(userId, blogId)) {
            return "Blog already liked";
        }

        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new RuntimeException("Blog not found"));


        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LikeBlog likeBlog = new LikeBlog();
        likeBlog.setUser(user);
        likeBlog.setBlog(blog);
        likeBlogRepository.save(likeBlog);
        return "liked blog successfully!";
    }




    @Transactional
    public String unLikedBlog(Long userId, Long blogId) {
        if (!likeBlogRepository.existsByUserIdAndBlogId(userId, blogId)) {
            return "Blog not liked";
        }

        likeBlogRepository.deleteByUserIdAndBlogId(userId, blogId);

        return "UnLiked successfully!";
    }

    public boolean isBlogLiked(Long userId, Long blogId) {
        return likeBlogRepository.existsByUserIdAndBlogId(userId, blogId);
    }

    public Long getNumbLike(Long blogId) {
        return likeBlogRepository.countByBlogId(blogId);
    }
    public List<Saved> getLikeBlogs(Long userId) {
        return likeBlogRepository.findByUserId(userId);
    }

}

