package com.findmymeme.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "로그인 요청 DTO")
@Getter
@NoArgsConstructor
public class LoginRequest {

    @Schema(description = "사용자 아이디", example = "meme_king", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{username.notblank}")
    private String username;

    @Schema(description = "비밀번호", example = "password123!", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{password.notblank}")
    private String password;

    @Builder
    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
