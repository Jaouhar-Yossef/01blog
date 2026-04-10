package com.dto.Request;

import java.util.UUID;

import com.util.BlogStatus;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
public class UpdateBlogsStatusRequestDTO {
    @NotNull(message = "blog_id must not be null")
    private UUID blog_id;
    
    @NotNull(message = "report_id must not be null")
    private Long report_id;

    @NotNull(message = "status must not be null")
    private BlogStatus status;
}