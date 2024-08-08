package com.findmymeme.exception;

import lombok.Getter;

@Getter
public class FindMyMemeException extends RuntimeException {

    private final ErrorCode errorCode;
    public FindMyMemeException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
