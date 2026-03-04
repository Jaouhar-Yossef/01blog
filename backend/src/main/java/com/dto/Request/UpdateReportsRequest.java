package com.dto.Request;

import java.util.UUID;

import lombok.Data;

@Data
public class UpdateReportsRequest {
    private UUID blog_id;
    private Long report_id;
    private String status;
}
