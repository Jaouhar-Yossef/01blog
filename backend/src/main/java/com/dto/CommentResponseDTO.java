package com.dto;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class CommentResponseDTO {
    private String comment;
    private String urlString;
    private String CreatorUsername;
}
