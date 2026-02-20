package com.dto.Response;


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
    private String status;
}
