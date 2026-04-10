package com.dto.Request;

import java.util.UUID;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateReportsRequest {

    @NotNull(message = "blog_id must not be null")
    private UUID blog_id;

    @NotNull(message = "report_id must not be null")
    private Long report_id;

    @NotNull(message = "status must not be null")
    private String status;
}