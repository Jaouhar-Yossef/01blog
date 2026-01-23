package com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.entity.MediaBlog;

@Repository
public interface MediaBlogRepository extends JpaRepository<MediaBlog, Long> {
}
