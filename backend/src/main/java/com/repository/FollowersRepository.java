package com.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.entity.Followers;

@Repository
public interface FollowersRepository extends JpaRepository<Followers , Long> {
    List<Followers> findByFollowed_Id(UUID userId);
    List<Followers> findByFollower_Id(UUID userId);
    boolean existsByFollower_IdAndFollowed_Id(UUID userId , UUID user_Id );
    long countByFollowed_Id(UUID userId);  
    long countByFollower_Id(UUID userId);
    void deleteByFollower_IdAndFollowed_Id(UUID followerId, UUID followedId);   
}
