package com.repository.Blogs;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.entity.Blogs.Blog;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {
    public List<Blog> findByCreatedById(Long userId);
    
}   