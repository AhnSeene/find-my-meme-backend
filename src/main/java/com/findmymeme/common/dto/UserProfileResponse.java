package com.findmymeme.common.dto;

import com.findmymeme.user.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserProfileResponse {
    private String username;
    private String profileImageUrl;
    private String role;

    @Builder
    public UserProfileResponse(String username, String profileImageUrl, String role) {
        this.username = username;
        this.profileImageUrl = profileImageUrl;
        this.role = role;
    }

    public UserProfileResponse(User user) {
        this.username = user.getUsername();
        this.profileImageUrl = user.getProfileImageUrl();
        this.role = user.getRole().name();
    }
}
