package com.dto.Request;

import com.util.UserStatus;

import lombok.*;

@Data
public class UpdateStatusBlogRequest {
    @NonNull
    private UserStatus status;
    @NonNull
    private String username;
}
