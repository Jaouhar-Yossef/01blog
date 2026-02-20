package com.dto.Response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private String username;
    private String imageUrl;
    private String role;
    private String tokeString;
}
