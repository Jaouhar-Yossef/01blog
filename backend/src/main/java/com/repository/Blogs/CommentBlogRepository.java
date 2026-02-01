package com.repository.Blogs;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.entity.Blogs.CommentBlog;

@Repository
public interface CommentBlogRepository extends JpaRepository<CommentBlog ,  Long> {
    
}
