package com.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.entity.Notifications;
import com.entity.User;
import com.entity.Blogs.Blog;
import com.util.TypeNotifications;

@Repository
public interface NotificationsRepository extends JpaRepository<Notifications, Long> {
    List<Notifications> findByIntendedUser_id(UUID userId, Pageable pageable);

    @Query("SELECT n FROM Notifications n " +
            "WHERE n.type = :type " +
            "AND n.intendedBlog = :blog " +
            "AND n.intendedUser = :user ")
    Optional<Notifications> findActiveNotification(
            @Param("type") TypeNotifications type,
            @Param("blog") Blog intendedBlog,
            @Param("user") User intendedUser);

    @Query("SELECT n FROM Notifications n " +
            "WHERE n.type = :type " +
            "AND n.intendedBlog = :blog " +
            "AND n.creatorNf = :creatorNf " +
            "AND n.intendedUser = :user ")
    List<Notifications> findNotification(
            @Param("type") TypeNotifications type,
            @Param("blog") Blog intendedBlog,
            @Param("user") User intendedUser,
            @Param("creatorNf") User creatorNf);
            

    void deleteByTypeAndIntendedBlogAndIntendedUser(
            TypeNotifications type,
            Blog intendedBlog,
            User intendedUser);

}