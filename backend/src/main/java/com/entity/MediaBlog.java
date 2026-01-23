package com.entity;

import com.util.TypeMedia;
import jakarta.persistence.*;

@Entity
@Table(name = "blog_media")
public class MediaBlog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private TypeMedia type;
    private String url;   

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "blog_id")
    private Blog blog;

    public String getFileName() {return fileName;}
    public TypeMedia getType() {return type;}
    public String getUrl() {return url;}

    public void setFileName(String fileName) {this.fileName = fileName;}
    public void setType(TypeMedia type) {this.type = type;}
    public void setUrl(String url) {this.url = url;}
    public void setBlog(Blog blog) {this.blog = blog;}
}
