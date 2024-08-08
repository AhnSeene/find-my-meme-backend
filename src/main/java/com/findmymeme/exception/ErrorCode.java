package com.findmymeme.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "INVALID INPUT VALUE"),

    ALREADY_EXIST_USERNAME(HttpStatus.BAD_REQUEST, "이미 존재하는 아이디입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
