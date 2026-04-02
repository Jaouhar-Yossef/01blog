package com.security;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class ImgProfileValidator {

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/png",
            "image/jpeg",
            "image/webp"
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
            if (file == null || file.isEmpty()) {
                throw new RuntimeException("Empty file");
            }

            String contentType = file.getContentType();
            if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
                throw new RuntimeException("Invalid file type");
            }

            byte[] bytes = file.getBytes();
            String content = new String(bytes, StandardCharsets.UTF_8).toLowerCase();

            for (String pattern : FORBIDDEN_PATTERNS) {
                if (content.contains(pattern)) {
                    throw new RuntimeException("Malicious content detected");
                }
            }

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

            // JPEG
            else if (header.length >= 2 &&
                    header[0] == (byte) 0xFF &&
                    header[1] == (byte) 0xD8) {
                valid = true;
            }

            // WEBP
            else if (header.length >= 12 &&
                    header[0] == 0x52 &&
                    header[1] == 0x49 &&
                    header[2] == 0x46 &&
                    header[3] == 0x46 &&
                    header[8] == 0x57 &&
                    header[9] == 0x45 &&
                    header[10] == 0x42 &&
                    header[11] == 0x50) {
                valid = true;
            }

            if (!valid) {
                throw new RuntimeException("Invalid image file");
            }

        } catch (IOException e) {
            throw new RuntimeException("File read error", e);
        }
    }
}