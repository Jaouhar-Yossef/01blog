package com.dto;

import java.util.UUID;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReportsRequest {
    private UUID blog_id;
    private Long report_id;
    private String status;
}
