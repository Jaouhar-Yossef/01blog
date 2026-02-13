package com.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsersToAdminDTO {
    private String username;
    private String email;
    private String role;
    private String status;
}
