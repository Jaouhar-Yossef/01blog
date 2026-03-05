package com.repository.Blogs;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.entity.Blogs.Saved;

@Repository
public interface SavedRepository extends JpaRepository<Saved, Long> {
    boolean existsByUserIdAndBlogId(UUID userId, UUID blogId);

    void deleteByUserIdAndBlogId(UUID userId, UUID blogId);

    long countByBlogId(UUID blogId);

    List<Saved> findByUserId(UUID userId);

    List<Saved> findSavedBlogsByUserId(UUID userId, Pageable pageable);

    @Query("""
                SELECT s.blog.id
                FROM Saved s
                WHERE s.user.id = :userId
                AND s.blog.id IN :blogIds
            """)
    List<UUID> findSavedBlogIds(
            @Param("userId") UUID userId,
            @Param("blogIds") List<UUID> blogIds);
}