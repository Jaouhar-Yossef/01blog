package com.repository;

import com.entity.Blog;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {
    public List<Blog> findByCreatedById(Long userId);
}