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

    public void saveMedia(Blog blog, BlogRequest blogRequest) {

        if (blogRequest.getFiles() == null || blogRequest.getFiles().isEmpty()) {
            return; 
        }

        String baseDir = System.getProperty("user.dir"); 
        String uploadDir = baseDir + File.separator + "uploads" + File.separator;

        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            System.out.println("Upload folder not created, blog will be saved without media: " + e.getMessage());
            return;
        }

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

            String originalFileName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "file_" + System.currentTimeMillis();
            media.setFileName(originalFileName);

            String contentType = file.getContentType();
            if (contentType != null && contentType.startsWith("video")) {
                media.setType(TypeMedia.VIDEO);
            } else {
                media.setType(TypeMedia.IMAGE);
            }

            try {
                String filePath = uploadDir + System.currentTimeMillis() + "_" + originalFileName;
                file.transferTo(new File(filePath));

                media.setUrl(filePath);
                mediaBlogRepository.save(media);
            } catch (IOException e) {
                System.out.println("Error uploading file " + originalFileName + ": " + e.getMessage());
            }
        }
    }
}
