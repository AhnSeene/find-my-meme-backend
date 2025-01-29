package com.findmymeme.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginResponse {

    private String username;
    private String role;

    public LoginResponse(String username, String role) {
        this.username = username;
        this.role = role;
    }

    public static LoginResponse fromLoginDto(LoginDto loginDto) {
        return new LoginResponse(loginDto.getUsername(), loginDto.getRole());
    }
}
