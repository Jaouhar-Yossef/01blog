package com.dto.Request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LikeOrSaveBlogRequest {
    @NotNull(message = "id_blog cannot be null")
    private UUID id_blog;
}