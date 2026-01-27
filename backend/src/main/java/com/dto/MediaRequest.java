package com.dto;

import org.springframework.web.multipart.MultipartFile;
import com.util.TypeMedia;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

import lombok.*;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MediaRequest {

    @NotNull(message = "File must not be null")
    private MultipartFile file; 

    @NotBlank(message = "URL must not be blank")
    private String url;       

    @NotNull(message = "Type must not be null")
    private TypeMedia type;   
}
