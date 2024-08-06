package com.findmymeme.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class FindMyMemeExceptionHandler {

    @ExceptionHandler(FindMyMemeException.class)
    public ResponseEntity<ErrorResponse> handleFindMyMemeException(final FindMyMemeException e) {
        final ErrorCode errorCode = e.getErrorCode();
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .status(errorCode.getHttpStatus())
                .message(errorCode.getMessage())
                .build();
        log.error("Error: code={}, message={}", errorCode, errorResponse.getMessage(), e);
        return new ResponseEntity<>(errorResponse, errorCode.getHttpStatus());
    }
}
