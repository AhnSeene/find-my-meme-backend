package com.findmymeme.user.dto;

import com.findmymeme.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "회원가입 응답 DTO")
@Getter
@AllArgsConstructor
public class SignupResponse {

    @Schema(description = "가입된 사용자 아이디", example = "memeking")
    private final String username;
    @Schema(description = "가입된 사용자 이메일", example = "test@example.com")
    private final String email;


    public SignupResponse(final User user) {
        this.username = user.getUsername();
        this.email = user.getEmail();
    }
}
