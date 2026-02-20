package com.dto.Request;

import lombok.*;

import java.util.UUID;

import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequest {

    @NotBlank(message = "type cannot be empty")
    private String type;

    @NotBlank(message = "Reason cannot be empty")
    @Size(max = 200, message = "Reason cannot exceed 200 characters")
    private String reason;

    private UUID reportedBlog;
    private String reportedUser;
}
