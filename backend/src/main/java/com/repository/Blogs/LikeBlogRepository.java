package com.repository.Blogs;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.entity.Blogs.LikeBlog;
import com.entity.Blogs.Saved;


@Repository
public interface LikeBlogRepository extends JpaRepository<LikeBlog ,  Long>{
    boolean existsByUserIdAndBlogId(Long userId, Long blogId);
    void deleteByUserIdAndBlogId(Long userId, Long blogId);
    long countByBlogId(Long blogId);
    List<Saved> findByUserId(Long userId);
}