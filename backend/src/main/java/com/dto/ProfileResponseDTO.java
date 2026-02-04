package com.dto;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponseDTO {

    private String username;
    private String email;
    private String imageUrl;
    private String tokeString;
    private String Bio;
    private boolean notYou;

    private Long nmbFollowers;
    private Long nmbBlogs;
}