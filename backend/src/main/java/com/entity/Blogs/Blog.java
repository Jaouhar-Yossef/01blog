package com.entity.Blogs;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.entity.Notifications;
import com.entity.Report;
import com.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.util.BlogStatus;

import lombok.*;

import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "blogs")
public class Blog {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    private String title;

    @Column(length = 2000)
    private String content;

    @Column(updatable = true , nullable = false)
    @Enumerated(EnumType.STRING)
    private BlogStatus status;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    @JsonIgnore
    private User createdBy;

    @Builder.Default
    @OneToMany(mappedBy = "blog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MediaBlog> medias = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "blog", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Saved> savedBlogs = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "blog", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<LikeBlog> likedBlogs = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "blog", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<CommentBlog> comentBlogs = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "reportedBlog", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Report> reports = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "intendedBlog", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Notifications> notifications = new ArrayList<>();

    public void addMedia(MediaBlog media) {
        medias.add(media);
        media.setBlog(this);
    }

    public void removeMedia(MediaBlog media) {
        medias.remove(media);
        media.setBlog(this);
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
