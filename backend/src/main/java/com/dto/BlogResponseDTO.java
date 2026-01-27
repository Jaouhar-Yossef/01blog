package com.dto;

import java.util.List;
import lombok.*;

@Data
@NoArgsConstructor
public class BlogResponseDTO {
    private Long id;
    private String title;
    private String content;
    private String createdByUsername;
    private List<MediaDTO> media;

    public BlogResponseDTO(Long id, String title, String content, String createdByUsername, List<MediaDTO> media) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdByUsername = createdByUsername;
        this.media = media;
    }
}
