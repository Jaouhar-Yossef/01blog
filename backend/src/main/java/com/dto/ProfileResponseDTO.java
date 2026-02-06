package com.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponseDTO {
    private String username;
    private String imageUrl;
    private boolean isYourProfile;
    private boolean isFollower;
    private boolean isFollowing;
    private Long CountFollowers;
    private Long CountFollowing;
    private Long BlogsCont;


}