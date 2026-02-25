package com.repository;


import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.entity.Notifications;

@Repository
public interface NotificationsRepository extends JpaRepository<Notifications , Long> {
    List<Notifications> findByIntendedUser_id(UUID userId , Pageable pageable);
}