package com.findmymeme.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginResponse {

    private String username;
    private String accessToken;
    private String refreshToken;
    private String role;

    @Builder
    public LoginResponse(String username, String accessToken, String refreshToken, String role) {
        this.username = username;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.role = role;
    }
}
