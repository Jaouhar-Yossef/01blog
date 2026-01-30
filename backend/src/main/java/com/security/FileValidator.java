package com.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileValidator {

    private static final Set<String> ALLOWED_TYPES = Set.of(
        "image/png",
        "image/jpeg",
        "image/webp",
        "video/mp4",
        "video/webm"
    );

    private static final List<String> FORBIDDEN_PATTERNS = List.of(
        "<script",
        "<?php",
        "#!/bin",
        "eval(",
        "document.write"
    );

    public void validate(MultipartFile file) {

        try {
            // 1️⃣ Empty
            if (file == null || file.isEmpty()) {
                throw new RuntimeException("Empty file");
            }

            // 2️⃣ Content-Type
            String contentType = file.getContentType();
            if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
                throw new RuntimeException("Invalid file type");
            }

            // 3️⃣ Script detection
            byte[] bytes = file.getBytes();
            String content = new String(bytes, StandardCharsets.UTF_8).toLowerCase();

            for (String pattern : FORBIDDEN_PATTERNS) {
                if (content.contains(pattern)) {
                    throw new RuntimeException("Malicious script detected");
                }
            }

            // 4️⃣ Magic bytes
            byte[] header = file.getInputStream().readNBytes(12);
            boolean valid = false;

            // PNG
            if (header.length >= 4 &&
                header[0] == (byte) 0x89 &&
                header[1] == 0x50 &&
                header[2] == 0x4E &&
                header[3] == 0x47) {
                valid = true;
            }
            // JPG
            else if (header.length >= 2 &&
                     header[0] == (byte) 0xFF &&
                     header[1] == (byte) 0xD8) {
                valid = true;
            }
            // GIF
            else if (header.length >= 3 &&
                     header[0] == 0x47 &&
                     header[1] == 0x49 &&
                     header[2] == 0x46) {
                valid = true;
            }
            // MP4
            else if (header.length >= 8) {
                String ftyp = new String(header, 4, 4);
                if ("ftyp".equals(ftyp)) {
                    valid = true;
                }
            }

            if (!valid) {
                throw new RuntimeException("File binary does not match image/video");
            }

        } catch (IOException e) {
            throw new RuntimeException("File read error");
        }
    }
}
