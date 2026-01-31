package com.repository.Blogs;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.entity.Blogs.ComentBlog;

@Repository
public interface ComentBlogRepositoryextends extends JpaRepository<ComentBlog ,  Long> {
    
}
