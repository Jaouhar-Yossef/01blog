package com.dto.Request;

import java.util.UUID;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LikeOrSaveBlogRequest {
    private UUID id_blog;
}