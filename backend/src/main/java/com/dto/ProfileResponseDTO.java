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

    public ProfileResponseDTO(String username , String imageUrl ,boolean isYourProfile , boolean isFollower , boolean isFollowing , Long BlogsCont) {
        this.username = username;
        this.imageUrl = imageUrl;
        this.isYourProfile = isYourProfile;
        this.isFollower = isFollower;
        this.isFollowing = isFollowing;
        this.BlogsCont = BlogsCont;
    }

}