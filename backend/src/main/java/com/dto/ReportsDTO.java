package com.dto;
import lombok.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportsDTO {
    private Long id;
    private String type;
    private String created_by;  
    private UUID reported_blog;
    private String reported_user;
    private String reason;
    private String status;
}
