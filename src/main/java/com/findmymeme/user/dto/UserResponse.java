package com.findmymeme.user.dto;

import com.findmymeme.user.User;
import lombok.Getter;

@Getter
public class UserResponse {

    private String username;
    private String email;

    public UserResponse(User user) {
        this.username = user.getUsername();
        this.email = user.getEmail();
    }
}
