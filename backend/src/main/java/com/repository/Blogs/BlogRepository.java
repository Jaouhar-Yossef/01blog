package com.repository.Blogs;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.entity.Blogs.Blog;

@Repository
public interface BlogRepository extends JpaRepository<Blog, UUID> {
    public List<Blog> findByCreatedById(UUID userId);

    List<Blog> findByCreatedById(UUID userId, Pageable pageable);

    Long countByCreatedBy_Id(UUID userId);

    @Query("SELECT b.createdBy.id as userId, COUNT(b) as blogCount " +
            "FROM Blog b " +
            "WHERE b.createdBy.id IN :userIds " +
            "GROUP BY b.createdBy.id")
    List<UserBlogCount> countBlogsForUsers(@Param("userIds") List<UUID> userIds);
}