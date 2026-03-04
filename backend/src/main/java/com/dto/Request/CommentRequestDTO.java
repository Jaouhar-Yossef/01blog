package com.dto.Request;

import java.util.UUID;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CommentRequestDTO {

    @NotNull(message = "blog id cannot be null")
    private UUID id_blog;

    @NotBlank(message = "comment cannot be empty")
    @Size(max = 300, message = "comment cannot exceed 300 characters")
    @Size(min = 1, message = "comment must be at least 1 character")
    private String comment;
}