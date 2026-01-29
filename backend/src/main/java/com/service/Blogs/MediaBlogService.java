package com.service.Blogs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dto.BlogRequest;
import com.entity.Blogs.Blog;
import com.entity.Blogs.MediaBlog;
import com.repository.Blogs.MediaBlogRepository;
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

        String uploadDir = System.getProperty("user.dir") + File.separator + "uploads";

        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory", e);
        }

        int maxFiles = 5;
        long maxFileSize = 50 * 1024 * 1024;
        int fileCount = 0;

        for (MultipartFile file : blogRequest.getFiles()) {

            if (file.isEmpty()) continue;

            fileCount++;
            if (fileCount > maxFiles) break;
            if (file.getSize() > maxFileSize) continue;

            MediaBlog media = new MediaBlog();
            media.setBlog(blog);

            String originalName = file.getOriginalFilename();
            if (originalName == null) originalName = "file";

            String fileName = UUID.randomUUID() + "_" + originalName;
            String contentType = file.getContentType();
            if (contentType != null && contentType.startsWith("video")) {
                media.setType(TypeMedia.VIDEO);
            } else {
                media.setType(TypeMedia.IMAGE);
            }

            try {
                Path filePath = Paths.get(uploadDir, fileName);
                Files.copy(file.getInputStream(), filePath);

                media.setFileName(fileName);
                media.setUrl("/uploads/" + fileName);

                mediaBlogRepository.save(media);

            } catch (IOException e) {
                System.out.println("Failed to upload file: " + originalName);
            }
        }
    }
}
