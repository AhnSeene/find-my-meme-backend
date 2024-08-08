package com.findmymeme.user.dto;

import com.findmymeme.user.User;
import lombok.Getter;

@Getter
public class UserResponse {

    private final String username;
    private final String email;

    public UserResponse(final User user) {
        this.username = user.getUsername();
        this.email = user.getEmail();
    }
}
