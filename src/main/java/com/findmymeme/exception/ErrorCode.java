package com.findmymeme.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),

    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "잘못된 입력값입니다."),

    ALREADY_EXIST_USERNAME(HttpStatus.BAD_REQUEST, "이미 존재하는 아이디입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
