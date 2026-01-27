package com.dto;

import java.util.List;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
public class BlogResponseDTO {
    private Long id;
    private String title;
    private String status;
    private String content;
    private boolean saved;
    private String createdByUsername;
    private List<MediaDTO> media;


    public BlogResponseDTO(Long id, String title, String status, String content, boolean saved, String createdByUsername, List<MediaDTO> media) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.content = content;
        this.saved = saved;
        this.createdByUsername = createdByUsername;
        this.media = media;
    }
}
