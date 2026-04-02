package com.dto.Response;

import com.util.UserStatus;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private String username;
    private String email;
    private String imageUrl;
    private String role;
    private UserStatus status;
    private String tokeString;
}
