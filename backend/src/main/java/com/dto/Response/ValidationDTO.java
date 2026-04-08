package com.dto.Response;


import com.util.UserStatus;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ValidationDTO {
    private String username;
    private String imageUrl;
    private String role;
    private UserStatus status;
    private String email;
}
