package com.repository;

import com.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmailOrUsername(String email, String username);

    @Query("""
        SELECT u
        FROM User u
        WHERE u.status <> 'BANNED'
        ORDER BY u.createdAt DESC
    """)
    List<User> findUsers(Pageable pageable);

    List<User> findByUsernameContainingIgnoreCase( String searchWord ,  Pageable pageable);
}
