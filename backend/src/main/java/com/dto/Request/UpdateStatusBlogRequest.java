package com.dto.Request;

import com.util.UserStatus;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStatusBlogRequest {
    @NonNull
    private UserStatus status;
    @NonNull
    private String username;
}
