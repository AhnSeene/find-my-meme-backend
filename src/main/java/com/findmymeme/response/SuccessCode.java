package com.findmymeme.response;

import org.springframework.http.HttpStatus;

import java.util.ResourceBundle;

public enum SuccessCode {
    USER_SIGNUP_SUCCESS(HttpStatus.CREATED);

    private final HttpStatus status;
    private static final ResourceBundle messages = ResourceBundle.getBundle("messages");

    SuccessCode(HttpStatus status) {
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return messages.getString(name());
    }
}
