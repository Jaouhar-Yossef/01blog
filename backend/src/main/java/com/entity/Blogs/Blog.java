package com.entity.Blogs;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

import com.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "blogs")
public class Blog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    
    @Column(length = 2000)
    private String content;

    private String status;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    @JsonIgnore
    private User createdBy;
    
    @OneToMany(mappedBy = "blog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MediaBlog> medias = new ArrayList<>();

    public void addMedia(MediaBlog media) {
        medias.add(media);
        media.setBlog(this); 
    }

    public static Object builder() {
        throw new UnsupportedOperationException("Unimplemented method 'builder'");
    }
}
