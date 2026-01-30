package com.dto;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LikeOrSaveBlogRequest {
    private Long id_blog;
}