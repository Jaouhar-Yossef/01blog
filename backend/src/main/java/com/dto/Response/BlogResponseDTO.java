package com.dto.Response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.util.BlogStatus;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BlogResponseDTO {
    private UUID id;
    private String title;
    private BlogStatus status;
    private String content;
    private boolean saved;
    private boolean liked;
    private Long numbLiked;
    private String createdByUsername;
    private String createdByUrlimg;
    private List<MediaDTO> media;
    private LocalDateTime creat_at;  
}
