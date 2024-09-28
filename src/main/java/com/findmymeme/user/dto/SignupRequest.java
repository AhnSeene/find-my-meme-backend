package com.findmymeme.user.dto;

import com.findmymeme.user.domain.Role;
import com.findmymeme.user.domain.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SignupRequest {

    @NotBlank(message = "{username.notblank}")
    @Size(min = 5, max = 20, message = "{username.size}")
    @Pattern(regexp = "^[a-z0-9]+$", message = "{username.pattern}")
    private final String username;

    @NotBlank(message = "{password.notblank}")
    @Size(min = 8, max = 16, message = "{password.size}")
    @Pattern(
            regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
            message = "{password.pattern}"
    )
    private final String password;

    @NotBlank(message = "{email.notBlank}")
    @Email(message = "{email.pattern}")
    @Size(min = 5, max = 50, message = "{email.size}")
    private final String email;

    @Builder
    public SignupRequest(final String username, final String password, final String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public static User toEntity(final SignupRequest signupRequest, final String encodedPassword, String defaultProfileImageUrl) {
        return User.builder()
                .username(signupRequest.getUsername())
                .password(encodedPassword)
                .email(signupRequest.getEmail())
                .role(Role.ROLE_USER)
                .profileImageUrl(defaultProfileImageUrl)
                .build();
    }

    public static User toEntity(final SignupRequest signupRequest, final Role role, final String encodedPassword, String defaultProfileImageUrl) {
        return User.builder()
                .username(signupRequest.getUsername())
                .password(encodedPassword)
                .email(signupRequest.getEmail())
                .role(role)
                .profileImageUrl(defaultProfileImageUrl)
                .build();
    }
}
