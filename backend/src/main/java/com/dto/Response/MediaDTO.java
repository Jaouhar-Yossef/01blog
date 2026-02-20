package com.dto.Response;

import com.util.TypeMedia;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MediaDTO {
    private String url;
    private String fileName;
    private TypeMedia type;
}    
