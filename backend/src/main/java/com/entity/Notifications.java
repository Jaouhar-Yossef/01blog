package com.entity;

import java.time.LocalDateTime;

import com.entity.Blogs.Blog;
import com.util.TypeNotifications;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notifications {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "causativeUser_id", nullable = true)
    private User creatorNf;

    @Column(updatable = false)
    @Enumerated(EnumType.STRING)
    private TypeNotifications type;

    @Column(updatable = false)
    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Intended_Blog_id", nullable = true)
    private Blog intendedBlog;

    @ManyToOne
    @JoinColumn(name = "Intended_User_id", nullable = false)
    private User intendedUser;


    @Column(nullable = false)
    private boolean active;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
