package com.findmymeme.user.dto;

import com.findmymeme.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignupResponse {

    private final String username;
    private final String email;


    public SignupResponse(final User user) {
        this.username = user.getUsername();
        this.email = user.getEmail();
    }
}
