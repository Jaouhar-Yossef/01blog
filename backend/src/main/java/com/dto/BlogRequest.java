package com.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public class BlogRequest {

    private String title;
    private String content;

    private List<MultipartFile> files;

    public String getTitle() { return title; }
    public String getContent() { return content; }
    public List<MultipartFile> getFiles() { return files; }

    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
    public void setFiles(List<MultipartFile> files) { this.files = files; }
}
