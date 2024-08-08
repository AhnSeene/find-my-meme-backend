package com.findmymeme.exception;

import com.findmymeme.response.ApiResponse;
import com.findmymeme.response.ResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class FindMyMemeExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(FindMyMemeException.class)
    public ResponseEntity<ApiResponse<Void>> handleFindMyMemeException(final FindMyMemeException e) {
        final ErrorCode errorCode = e.getErrorCode();
        log.error("Error: code={}, message={}", errorCode, errorCode.getMessage(), e);
        return ResponseUtil.error(null, errorCode); //TODO null처리 변경
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(final Exception e) {
        log.error("Unexpected error occurred: {}", e.getMessage(), e);
        return ResponseUtil.error(null, ErrorCode.INTERNAL_SERVER_ERROR);
    }
}
