package com.findmymeme.user.dto;

import com.findmymeme.user.domain.Role;
import com.findmymeme.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "회원가입 요청 DTO")
@Getter
public class SignupRequest {

    @Schema(description = "사용자 아이디 (5~20자, 영문 소문자, 숫자)", example = "memeking", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{username.notblank}")
    @Size(min = 5, max = 20, message = "{username.size}")
    @Pattern(regexp = "^[a-z0-9]+$", message = "{username.pattern}")
    private final String username;

    @Schema(description = "비밀번호 (8~16자, 영문, 숫자, 특수문자 포함)", example = "password123!", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{password.notblank}")
    @Size(min = 8, max = 16, message = "{password.size}")
    @Pattern(
            regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
            message = "{password.pattern}"
    )
    private final String password;

    @Schema(description = "이메일 주소", example = "test@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{email.notblank}")
    @Email(message = "{email.pattern}")
    @Size(min = 5, max = 50, message = "{email.size}")
    private final String email;

    @Builder
    public SignupRequest(final String username, final String password, final String email) {
        this.username = username;
        this.password = password;
        this.email = email;
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
