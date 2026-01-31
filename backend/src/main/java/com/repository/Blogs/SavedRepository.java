package com.repository.Blogs;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.entity.Blogs.Saved;

@Repository
public interface SavedRepository  extends JpaRepository<Saved ,  Long>{
    boolean existsByUserIdAndBlogId(UUID userId, UUID blogId);
    void deleteByUserIdAndBlogId(UUID userId, UUID blogId);
    long countByBlogId(UUID blogId);
    List<Saved> findByUserId(UUID userId);
}