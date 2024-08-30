package com.findmymeme.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.ResourceBundle;

public enum SuccessCode {
    SIGNUP(HttpStatus.CREATED),
    LOGIN(HttpStatus.OK),
    USER_INFO(HttpStatus.OK),
    USER_PROFILE_IMAGE_UPDATE(HttpStatus.OK),
    USER_DUPLICATE_VALIDATION(HttpStatus.OK),

    FILE_UPLOAD(HttpStatus.OK),

    FIND_POST_UPLOAD(HttpStatus.CREATED),
    FIND_POST_GET(HttpStatus.OK),
    FIND_POST_AUTHOR_LIST(HttpStatus.OK),
    FIND_POST_LIST(HttpStatus.OK),
    FIND_POST_UPDATE(HttpStatus.OK),
    FIND_POST_FOUND(HttpStatus.OK),
    FIND_POST_DELETE(HttpStatus.OK),

    FIND_POST_COMMENT_UPLOAD(HttpStatus.CREATED),
    FIND_POST_COMMENT_GET(HttpStatus.OK),
    FIND_POST_COMMENT_LIST(HttpStatus.OK),
    FIND_POST_COMMENT_UPDATE(HttpStatus.OK),
    FIND_POST_COMMENT_DELETE(HttpStatus.OK),

    TAG_CREATE(HttpStatus.CREATED),
    TAG_LIST(HttpStatus.OK),

    MEME_POST_UPLOAD(HttpStatus.CREATED),
    MEME_POST_GET(HttpStatus.OK),
    MEME_POST_LIST(HttpStatus.OK),
    MEME_POST_RECOMMENDED_LIST(HttpStatus.OK),
    MEME_POST_AUTHOR_LIST(HttpStatus.OK),
    MEME_POST_LIKE(HttpStatus.OK),
    MEME_POST_DELETE(HttpStatus.OK);

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
