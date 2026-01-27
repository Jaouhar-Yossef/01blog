package com.dto;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import lombok.*;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogRequest {

    @NotBlank(message = "Title cannot be empty")
    @Size(max = 50, message = "Title cannot exceed 50 characters")
    private String title;

    @NotBlank(message = "Content cannot be empty")
    @Size(max = 2000, message = "Content cannot exceed 2000 characters")
    private String content;

    @Size(max = 5, message = "You can upload at most 5 files")
    private List<MultipartFile> files;
}
