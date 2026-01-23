package com.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "blog_media")
public class BlogMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private String fileType;   
    private String url;        

    @ManyToOne
    @JoinColumn(name = "blog_id")
    private Blog blog;
}
