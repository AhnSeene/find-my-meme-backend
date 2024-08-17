package com.findmymeme.response;

import org.springframework.http.HttpStatus;

import java.util.ResourceBundle;

public enum SuccessCode {
    USER_SIGNUP(HttpStatus.CREATED),

    FILE_UPLOAD(HttpStatus.OK),

    FIND_POST_UPLOAD(HttpStatus.CREATED),
    FIND_POST_GET(HttpStatus.OK),
    FIND_POST_LIST(HttpStatus.OK),
    FIND_POST_UPDATE(HttpStatus.OK),

    FIND_POST_COMMENT_UPLOAD(HttpStatus.CREATED),
    FIND_POST_COMMENT_GET(HttpStatus.OK),
    FIND_POST_COMMENT_LIST(HttpStatus.OK),
    FIND_POST_COMMENT_UPDATE(HttpStatus.OK),

    TAG_CREATE(HttpStatus.CREATED),
    TAG_LIST(HttpStatus.OK);

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
