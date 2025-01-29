package com.findmymeme.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginResponse {

    private String username;
    private String role;
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
