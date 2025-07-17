package com.findmymeme.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "프로필 이미지 변경 응답 DTO")
@Getter
public class UserProfileImageResponse {

    @Schema(description = "새로 변경된 프로필 이미지의 URL", example = "http://localhost:8080/images/profile/new_image.jpg")
    private String profileImageUrl;

    private UserProfileImageResponse(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public static UserProfileImageResponse from(String profileImageUrl, String fileUrl) {
        return new UserProfileImageResponse(fileUrl + profileImageUrl);
    }
}
