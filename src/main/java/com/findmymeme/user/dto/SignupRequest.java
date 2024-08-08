package com.findmymeme.user.dto;

import com.findmymeme.user.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SignupRequest {

    @NotBlank
    @Size(min = 5, max = 20)
    private final String username;

    @NotBlank
    @Size(min = 8, max = 16)
    private final String password;

    @NotBlank
    @Email
    @Size(max = 100)
    private final String email;

    @Builder
    public SignupRequest(final String username, final String password, final String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public static User toEntity(final SignupRequest signupRequest, final String encodedPassword) {
        return User.builder()
                .username(signupRequest.getUsername())
                .password(encodedPassword)
                .email(signupRequest.getEmail())
                .build();
    }
}
