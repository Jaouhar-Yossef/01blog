package com.dto.Response;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BlogsToAdminDTO {
    private UUID blogId;
    private String title;
    private String created_by;
    private String status;
    private LocalDateTime updated_at;
}
