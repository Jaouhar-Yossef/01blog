package com.dto.Request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DeleteReportsRequest {

    @NotNull(message = "report_id must not be null")
    private Long report_id;
}