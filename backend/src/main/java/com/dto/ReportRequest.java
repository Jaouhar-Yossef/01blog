package com.dto;

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
    @Size(max = 100, message = "Reason cannot exceed 100 characters")
    private String reason;
    
    private String reportedUser;

    private UUID reportedBlog;


}
