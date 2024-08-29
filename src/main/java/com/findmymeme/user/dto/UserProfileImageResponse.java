package com.findmymeme.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserProfileImageResponse {

    private String profileImageUrl;

    public UserProfileImageResponse(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
