package com.dto;

import org.springframework.web.multipart.MultipartFile;

import com.util.TypeMedia;

public class MediaRequest {

    private MultipartFile file; 
    private String url;       
    private TypeMedia type;   

    public MediaRequest() {}

    public MediaRequest(MultipartFile file, String url, TypeMedia type) {
        this.file = file;
        this.url = url;
        this.type = type;
    }

    public MultipartFile getFile() { return file; }
    public void setFile(MultipartFile file) { this.file = file; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public TypeMedia getType() { return type; }
    public void setType(TypeMedia type) { this.type = type; }
}
