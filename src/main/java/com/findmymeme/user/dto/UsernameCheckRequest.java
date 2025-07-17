package com.findmymeme.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Schema(description = "아이디 중복 확인 요청 DTO")
@NoArgsConstructor
public class UsernameCheckRequest {

    @Schema(description = "중복 확인할 아이디 (5~20자, 영문 소문자, 숫자)", example = "new_user", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{username.notblank}")
    @Size(min = 5, max = 20, message = "{username.size}")
    @Pattern(regexp = "^[a-z0-9]+$", message = "{username.pattern}")
    private String username;

    public UsernameCheckRequest(String username) {
        this.username = username;
    }
}
