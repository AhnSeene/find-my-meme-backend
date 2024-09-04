package com.findmymeme.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginResponse {

    private String username;
    private String accessToken;
    private String role;

    @Builder
    public LoginResponse(String username, String accessToken, String role) {
        this.username = username;
        this.accessToken = accessToken;
        this.role = role;
    }
}
