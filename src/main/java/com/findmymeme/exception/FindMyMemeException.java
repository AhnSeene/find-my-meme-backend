package com.findmymeme.exception;

import lombok.Getter;

@Getter
public class FindMyMemeException extends RuntimeException {

    private final ErrorCode errorCode;
    public FindMyMemeException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
