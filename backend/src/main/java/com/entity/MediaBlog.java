package com.entity;

import jakarta.persistence.*;
import com.util.TypeMedia;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
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

}
