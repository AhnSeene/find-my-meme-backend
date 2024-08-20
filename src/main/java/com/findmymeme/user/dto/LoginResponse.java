package com.findmymeme.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginResponse {

    private String accessToken;

    @Builder
    public LoginResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}