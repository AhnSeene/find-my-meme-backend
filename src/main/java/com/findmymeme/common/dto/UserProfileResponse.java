package com.findmymeme.common.dto;

import com.findmymeme.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "사용자 프로필 정보 응답 DTO")
@Getter
public class UserProfileResponse {
    @Schema(description = "사용자 아이디", example = "meme_king")
    private String username;
    @Schema(description = "프로필 이미지 URL", example = "http://localhost:8080/images/profile/default.jpg")
    private String profileImageUrl;
    @Schema(description = "사용자 권한", example = "ROLE_USER")
    private String role;

    @Builder
    public UserProfileResponse(String username, String profileImageUrl, String role) {
        this.username = username;
        this.profileImageUrl = profileImageUrl;
        this.role = role;
    }

    public static UserProfileResponse from(User user, String fileBaseUrl) {
        return UserProfileResponse.builder()
                .username(user.getUsername())
                .profileImageUrl(fileBaseUrl + user.getProfileImageUrl())
                .role(user.getRole().name())
                .build();
    }
}
