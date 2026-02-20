package com.dto.Request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStatusBlogRequest {
    private String status;
    private String username;    
}
