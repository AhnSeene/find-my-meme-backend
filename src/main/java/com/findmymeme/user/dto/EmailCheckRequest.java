package com.findmymeme.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EmailCheckRequest {

    @NotBlank(message = "{email.notBlank}")
    @Email(message = "{email.pattern}")
    @Size(min = 5, max = 50, message = "{email.size}")
    private String email;

    public EmailCheckRequest(String email) {
        this.email = email;
    }
}
