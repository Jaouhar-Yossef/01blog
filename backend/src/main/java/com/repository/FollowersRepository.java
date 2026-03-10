package com.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.entity.Followers;
import com.entity.Blogs.Blog;

@Repository
public interface FollowersRepository extends JpaRepository<Followers, Long> {
    List<Followers> findByFollowed_Id(UUID userId);

    List<Followers> findByFollowed_Id(UUID userId, Pageable pageable);

    List<Followers> findByFollower_Id(UUID userId);

    List<Followers> findByFollower_Id(UUID userId, Pageable pageable);

    boolean existsByFollower_IdAndFollowed_Id(UUID userId, UUID user_Id);

    @Query("""
            SELECT COUNT(f)
            FROM Followers f
            JOIN f.followed u
            WHERE f.follower.id = :userId
            AND u.status <> 'BANNED'
            """)
    long countFollowingNotBanned(UUID userId);

     @Query("""
            SELECT COUNT(f)
            FROM Followers f
            JOIN f.follower u
            WHERE f.followed.id = :userId
            AND u.status <> 'BANNED'
            """)
    long countFollowerNotBanned(@Param("userId") UUID userId);

    void deleteByFollower_IdAndFollowed_Id(UUID followerId, UUID followedId);

    @Query("""
                SELECT b
                FROM Followers f
                JOIN f.followed u
                JOIN u.blogs b
                WHERE f.follower.id = :userId
                AND f.followed.status <> 'BANNED'
                ORDER BY b.createdAt DESC
            """)
    List<Blog> findBlogsOfFollowedUsers(
            @Param("userId") UUID userId,
            Pageable pageable);

    @Query("SELECT f.followed.id FROM Followers f WHERE f.follower.id = :userId")
    List<UUID> findFollowedIdsByFollowerId(@Param("userId") UUID userId);

    @Query("SELECT f.follower.id FROM Followers f WHERE f.followed.id = :userId")
    List<UUID> findFollowerIdsByFollowedId(@Param("userId") UUID userId);

}
