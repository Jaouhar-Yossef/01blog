package com.dto.Response;
import lombok.*;
import java.util.UUID;

import com.util.ReportType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportsDTO {
    private Long id;
    private ReportType type;
    private String created_by;  
    private UUID reported_blog;
    private String reported_user;
    private String reason;
    private String status;
}
