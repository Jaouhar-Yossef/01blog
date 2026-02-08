package com.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileValidator {

    // ✅ Allowed MIME types including GIF
    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/png",
            "image/jpeg",
            "image/webp",
            "image/gif",
            "video/mp4",
            "video/webm");

    // ✅ Patterns to detect malicious scripts
    private static final List<String> FORBIDDEN_PATTERNS = List.of(
            "<script",
            "<?php",
            "#!/bin",
            "eval(",
            "document.write"
    );

    public void validate(MultipartFile file) {

        try {
            // 1️⃣ Check empty file
            if (file == null || file.isEmpty()) {
                throw new RuntimeException("Empty file");
            }

            // 2️⃣ Check MIME type
            String contentType = file.getContentType();
            if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
                throw new RuntimeException("Invalid file type");
            }

            // 3️⃣ Check for malicious scripts in file content
            byte[] bytes = file.getBytes();
            String content = new String(bytes, StandardCharsets.UTF_8).toLowerCase();

            for (String pattern : FORBIDDEN_PATTERNS) {
                if (content.contains(pattern)) {
                    throw new RuntimeException("Malicious script detected");
                }
            }

            // 4️⃣ Check file magic bytes
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
            // WEBP
            else if (header.length >= 12 &&
                    header[0] == 0x52 && // R
                    header[1] == 0x49 && // I
                    header[2] == 0x46 && // F
                    header[3] == 0x46 && // F
                    header[8] == 0x57 && // W
                    header[9] == 0x45 && // E
                    header[10] == 0x42 && // B
                    header[11] == 0x50) { // P
                valid = true;
            }
            // GIF
            else if (header.length >= 3 &&
                    header[0] == 0x47 && // G
                    header[1] == 0x49 && // I
                    header[2] == 0x46) { // F
                valid = true;
            }
            // MP4
            else if (header.length >= 8) {
                String ftyp = new String(header, 4, 4);
                if ("ftyp".equals(ftyp)) {
                    valid = true;
                }
            }
            // WEBM (optional)
            else if (header.length >= 4 &&
                    header[0] == 0x1A &&
                    header[1] == 0x45 &&
                    header[2] == (byte) 0xDF &&
                    header[3] == (byte) 0xA3) {
                valid = true;
            }

            if (!valid) {
                throw new RuntimeException("File binary does not match image/video");
            }

        } catch (IOException e) {
            throw new RuntimeException("File read error", e);
        }
    }
}
