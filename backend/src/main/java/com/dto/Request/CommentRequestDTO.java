package com.dto.Request;

import java.util.UUID;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class CommentRequestDTO {
    private UUID Id_blog;
    private String comment;
}
