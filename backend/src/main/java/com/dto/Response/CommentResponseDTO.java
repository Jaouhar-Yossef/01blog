package com.dto.Response;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class CommentResponseDTO {
    private Long id;
    private String comment;
    private String urlString;
    private String CreatorUsername;
}
