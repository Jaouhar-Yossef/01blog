package com.service.Blogs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dto.Request.BlogRequest;
import com.entity.Blogs.Blog;
import com.entity.Blogs.MediaBlog;
import com.repository.Blogs.MediaBlogRepository;
import com.security.FileValidator;
import com.util.TypeMedia;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Transactional
@RequiredArgsConstructor
@Service
public class MediaBlogService {

    private final MediaBlogRepository mediaBlogRepository;
    private final FileValidator fileValidator;

    public void saveMedia(Blog blog, BlogRequest blogRequest, String mode) {
        if (mode.equals("update")) {

            List<MediaBlog> oldMedia = new ArrayList<>(blog.getMedias());

            if (blogRequest.getFilesupdated() != null) {
                oldMedia.forEach(m -> {
                    if (!blogRequest.getFilesupdated().contains(m.getUrl())) {
                        deleteMedia(m.getId());
                    }
                });
            } else {
                oldMedia.forEach(m -> {
                    deleteMedia(m.getId());
                });
            }
        }

        if (blogRequest.getFiles() == null || blogRequest.getFiles().isEmpty()) {
            return;
        }

        String uploadDir = System.getProperty("user.dir") + File.separator + "uploads";

        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory");
        }

        int maxFiles = 5;
        long maxFileSize = 20 * 1024 * 1024;
        int fileCount = 0;

        for (MultipartFile file : blogRequest.getFiles()) {
            if (file.isEmpty())
                continue;
            fileCount++;
            if (fileCount > maxFiles)
                break;
            if (file.getSize() > maxFileSize) {
                throw new RuntimeException("File too large");
            }
            try {

                fileValidator.validate(file);

                MediaBlog media = new MediaBlog();
                media.setBlog(blog);

                String originalName = file.getOriginalFilename();
                if (originalName == null)
                    originalName = "file";

                String fileName = UUID.randomUUID() + "_" + originalName;
                String contentType = file.getContentType();

                if (contentType != null) {
                    if (contentType.startsWith("video")) {
                        media.setType(TypeMedia.VIDEO);
                    } else if (contentType.startsWith("image")) {
                        media.setType(TypeMedia.IMAGE);
                    } else {
                        throw new RuntimeException("Invalid file");
                    }
                } else {
                    throw new RuntimeException("File has no content type");
                }

                Path filePath = Paths.get(uploadDir, fileName);
                Files.copy(file.getInputStream(), filePath);

                media.setFileName(fileName);
                media.setUrl("/uploads/" + fileName);

                mediaBlogRepository.save(media);

            } catch (Exception e) {
                throw new RuntimeException("Invalid file");

            }
        }
    }

    public void deleteMedia(Long mediaId) {
        MediaBlog media = mediaBlogRepository.findById(mediaId)
                .orElseThrow(() -> new RuntimeException("Media not found with id: " + mediaId));

        Blog blog = media.getBlog();

        blog.removeMedia(media);

        String uploadDir = System.getProperty("user.dir") + File.separator + "uploads";
        Path filePath = Paths.get(uploadDir, media.getFileName());
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            System.out.println("Failed to delete file: " + media.getFileName());
        }
    }

    public void deleteBlogFiles(Blog blog) {
        String uploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator;
        for (MediaBlog media : blog.getMedias()) {
            File file = new File(uploadDir + media.getFileName());
            if (file.exists()) {
                boolean deleted = file.delete();
                if (!deleted) {
                    System.out.println("Failed to delete file: " + file.getAbsolutePath());
                }
            }
        }
    }

}
