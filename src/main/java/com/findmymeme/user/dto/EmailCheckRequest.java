package com.findmymeme.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Schema(description = "이메일 중복 확인 요청 DTO")
@NoArgsConstructor
public class EmailCheckRequest {

    @Schema(description = "중복 확인할 이메일 주소", example = "check@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{email.notBlank}")
    @Email(message = "{email.pattern}")
    @Size(min = 5, max = 50, message = "{email.size}")
    private String email;

    public EmailCheckRequest(String email) {
        this.email = email;
    }
}
