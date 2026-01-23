package com.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dto.BlogRequest;
import com.entity.Blog;
import com.entity.MediaBlog;
import com.repository.MediaBlogRepository;
import com.util.TypeMedia;

@Service
public class MediaBlogService {

    private final MediaBlogRepository mediaBlogRepository;

    public MediaBlogService(MediaBlogRepository mediaBlogRepository) {
        this.mediaBlogRepository = mediaBlogRepository;
    }

    public void saveMedia(Blog blog , BlogRequest blogRequest ) {

        String uploadDir = "backend/uploads/";
        boolean uploadDirExists = true;

        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            uploadDirExists = false;
            System.out.println("Upload folder not created, blog will be saved without media: " + e.getMessage());
        }

        if (uploadDirExists && blogRequest.getFiles() != null && !blogRequest.getFiles().isEmpty()) {

            int maxFiles = 5;
            long maxFileSize = 20 * 1024 * 1024; 
            int fileCount = 0;

            for (MultipartFile file : blogRequest.getFiles()) {
                if (file.isEmpty()) continue;
                fileCount++;
                if (fileCount > maxFiles) {
                    System.out.println("File limit reached, remaining files ignored");
                    break;
                }
                if (file.getSize() > maxFileSize) {
                    System.out.println("File " + file.getOriginalFilename() + " is too large, skipped");
                    continue;
                }

                MediaBlog media = new MediaBlog();
                media.setBlog(blog);
                
                media.setFileName(file.getOriginalFilename() != null ? file.getOriginalFilename() : "file_" + System.currentTimeMillis());
                
                if (file.getContentType() != null) {
                    if (file.getContentType().startsWith("image")) {
                        media.setType(TypeMedia.IMAGE);
                    } else if (file.getContentType().startsWith("video")) {
                        media.setType(TypeMedia.VIDEO);
                    } else {
                        media.setType(TypeMedia.IMAGE);
                    }
                } else {
                    media.setType(TypeMedia.IMAGE); 
                }

                
                try {
                    String filePath = uploadDir + System.currentTimeMillis() + "_" + media.getFileName();
                    file.transferTo(new File(filePath));
                    media.setUrl(filePath);
                    mediaBlogRepository.save(media);
                } catch (IOException e) {
                    System.out.println("Error uploading file " + media.getFileName() + ": " + e.getMessage());
                }
            }
        }
    }
}
