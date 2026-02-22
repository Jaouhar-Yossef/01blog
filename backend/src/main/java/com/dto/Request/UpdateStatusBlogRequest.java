package com.dto.Request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStatusBlogRequest {
    @NonNull
    private String status;
    @NonNull
    private String username;
}
