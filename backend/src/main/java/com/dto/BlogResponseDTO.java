package com.dto;

import java.util.List;

public class BlogResponseDTO {
    private String title;
    private String content;
    private String createdByUsername;

    private List<String> media; 

    public BlogResponseDTO(String title, String content, String createdByUsername) {
        this.title = title;
        this.content = content;
        this.createdByUsername = createdByUsername;
    }

    // getters
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getCreatedByUsername() { return createdByUsername; }
    public List<String> getmedia() { return media; }

}
