package com.findmymeme.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "로그인 응답 DTO")
@Getter
@NoArgsConstructor
public class LoginResponse {

    @Schema(description = "로그인한 사용자 아이디", example = "meme_king")
    private String username;
    @Schema(description = "사용자 권한", example = "ROLE_USER")
    private String role;
    @Schema(description = "발급된 Access Token", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWI...")
    private String accessToken;

    @Builder
    public LoginResponse(String username, String role, String accessToken) {
        this.username = username;
        this.role = role;
        this.accessToken = accessToken;
    }

    public static LoginResponse fromLoginDto(LoginDto loginDto) {
        return LoginResponse.builder()
                .username(loginDto.getUsername())
                .role(loginDto.getRole())
                .accessToken(loginDto.getAccessToken())
                .build();
    }
}
