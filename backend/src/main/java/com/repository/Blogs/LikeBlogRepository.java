package com.repository.Blogs;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.entity.Blogs.LikeBlog;
import com.entity.Blogs.Saved;

@Repository
public interface LikeBlogRepository extends JpaRepository<LikeBlog, Long> {
    boolean existsByUserIdAndBlogId(UUID userId, UUID blogId);

    void deleteByUserIdAndBlogId(UUID userId, UUID blogId);

    long countByBlogId(UUID blogId);

    List<Saved> findByUserId(UUID userId);

    @Query("""
                SELECT l.blog.id, COUNT(l)
                FROM LikeBlog l
                WHERE l.blog.id IN :blogIds
                GROUP BY l.blog.id
            """)
    List<Object[]> countLikesByBlogIds(@Param("blogIds") List<UUID> blogIds);

    @Query("""
                SELECT l.blog.id
                FROM LikeBlog l
                WHERE l.user.id = :userId
                AND l.blog.id IN :blogIds
            """)
    List<UUID> findLikedBlogIds(
            @Param("userId") UUID userId,
            @Param("blogIds") List<UUID> blogIds);
}