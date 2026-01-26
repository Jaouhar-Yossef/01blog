package com.entity;

import jakarta.persistence.*;
import com.util.TypeMedia;

@Entity
@Table(name = "blog_media")
public class MediaBlog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    @Enumerated(EnumType.ORDINAL)
    private TypeMedia type;


    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_id")
    private Blog blog;

    // --- Getters & Setters ---
    public Long getId() { return id; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public TypeMedia getType() { return type; }
    public void setType(TypeMedia type) { this.type = type; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public Blog getBlog() { return blog; }
    public void setBlog(Blog blog) { this.blog = blog; }
}
