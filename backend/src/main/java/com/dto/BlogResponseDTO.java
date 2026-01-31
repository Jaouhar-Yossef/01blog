package com.dto;

import java.util.List;
import java.util.UUID;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BlogResponseDTO {
    private UUID id;
    private String title;
    private String status;
    private String content;
    private boolean saved;
    private boolean liked;
    private Long numbLiked;
    private String createdByUsername;
    private List<MediaDTO> media;
}
