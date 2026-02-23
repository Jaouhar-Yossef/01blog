package com.dto.Response;

import com.util.UserStatus;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsersToAdminDTO {
    private String username;
    private String email;
    private String role;
    private UserStatus status;
}
