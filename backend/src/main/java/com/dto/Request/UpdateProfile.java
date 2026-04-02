package com.dto.Request;

import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateProfile {

    @NotBlank(message = "Username must not be blank")
    @Size(min = 3, max = 15, message = "Username must be At least 3 characters!")
    private String username;

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email should be valid")
    private String email;

    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    private MultipartFile image;
}