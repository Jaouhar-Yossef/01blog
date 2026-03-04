 package com.dto.Request;

import java.util.UUID;

import com.util.BlogStatus;

import lombok.Data;

@Data
public class  UpdateBlogsStatusRequestDTO {
    private UUID blog_id;
    private Long report_id;
    private BlogStatus status;
}