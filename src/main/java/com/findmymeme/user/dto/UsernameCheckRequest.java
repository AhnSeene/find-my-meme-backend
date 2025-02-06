package com.findmymeme.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UsernameCheckRequest {

    @NotBlank(message = "{username.notblank}")
    @Size(min = 5, max = 20, message = "{username.size}")
    @Pattern(regexp = "^[a-z0-9]+$", message = "{username.pattern}")
    private String username;

    public UsernameCheckRequest(String username) {
        this.username = username;
    }
}
