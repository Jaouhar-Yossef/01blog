package com.repository.Blogs;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.entity.Blogs.CommentBlog;

@Repository
public interface CommentBlogRepository extends JpaRepository<CommentBlog ,  Long> {
    public List<CommentBlog> findByBlogId(UUID id_blog , Pageable pageable);
}
