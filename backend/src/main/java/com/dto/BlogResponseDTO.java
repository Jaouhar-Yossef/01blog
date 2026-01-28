package com.dto;

import java.util.List;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BlogResponseDTO {
    private Long id;
    private String title;
    private String status;
    private String content;
    private boolean saved;
    private boolean liked;
    private Long NumbLiked;
    private String createdByUsername;
    private List<MediaDTO> media;

}
