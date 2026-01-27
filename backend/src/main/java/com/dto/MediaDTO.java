package com.dto;

import com.util.TypeMedia;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MediaDTO {
    private String url;
    private String fileName;
    private TypeMedia type;
}    
