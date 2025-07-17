package com.findmymeme.response;

import com.findmymeme.exception.ErrorCode;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class ApiResult<T> {

    private final boolean success;
    private final String message;
    private final String code;
    private final T data;

    public static <T> ApiResult<T> success(String message, T data) {
        return ApiResult.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResult<T> error(ErrorCode errorCode, T data) {
        return ApiResult.<T>builder()
                .success(false)
                .message(errorCode.getMessage())
                .code(errorCode.name())
                .data(data)
                .build();
    }

    public static <T> ApiResult<T> error(String message, T data) {
        return ApiResult.<T>builder()
                .success(false)
                .message(message)
                .data(data)
                .build();
    }
}
