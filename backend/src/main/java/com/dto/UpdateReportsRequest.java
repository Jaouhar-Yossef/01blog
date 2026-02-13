package com.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReportsRequest {
    private Long report_id;
    private String status;
}
