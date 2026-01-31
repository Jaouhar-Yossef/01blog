package com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.entity.Followers;

@Repository
public interface FollowersRepository extends JpaRepository<Followers , Long> {
    
}
