package com.findmymeme.user.dto;

import com.findmymeme.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Schema(description = "내 정보 조회 응답 DTO")
@Getter
@NoArgsConstructor
public class UserInfoResponse {
    @Schema(description = "사용자 ID", example = "1")
    private Long id;
    @Schema(description = "사용자 아이디", example = "meme_king")
    private String username;
    @Schema(description = "이메일 주소", example = "test@example.com")
    private String email;
    @Schema(description = "프로필 이미지 URL", example = "http://localhost:8080/images/profile/default.jpg")
    private String profileImageUrl;
    @Schema(description = "가입 일시", example = "2023-10-27T10:00:00")
    private LocalDateTime createdAt;
    @Schema(description = "사용자 권한", example = "ROLE_USER")
    private String roles;

    @Builder
    private UserInfoResponse(Long id, String username, String email, String profileImageUrl, LocalDateTime createdAt, String roles) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        this.createdAt = createdAt;
        this.roles = roles;
    }

    public static UserInfoResponse from(User user, String fileBaseUrl) {
        return UserInfoResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .profileImageUrl(fileBaseUrl + user.getProfileImageUrl())
                .createdAt(user.getCreatedAt())
                .roles(user.getRole().name())
                .build();
    }
}
