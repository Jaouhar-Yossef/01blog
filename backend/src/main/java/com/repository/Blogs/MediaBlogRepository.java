package com.repository.Blogs;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.entity.Blogs.MediaBlog;

@Repository
public interface MediaBlogRepository extends JpaRepository<MediaBlog, Long> {
}
