package com.findmymeme.exception;

import com.findmymeme.response.ApiResult;
import com.findmymeme.response.ResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class FindMyMemeExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(FindMyMemeException.class)
    public ResponseEntity<ApiResult<Void>> handleFindMyMemeException(final FindMyMemeException e) {
        final ErrorCode errorCode = e.getErrorCode();
        log.error("Error: code={}, message={}", errorCode, errorCode.getMessage(), e);
        return ResponseUtil.error(null, errorCode); //TODO null처리 변경
    }

    /**
     * 유효성 검사에서 실패할 때 발생한다.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResult<List<FieldErrorDto>>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<FieldErrorDto> fieldErrors = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> FieldErrorDto.builder()
                        .field(fieldError.getField())
                        .value(fieldError.getRejectedValue() != null ? fieldError.getRejectedValue().toString() : null)
                        .reason(messageSource.getMessage(fieldError, LocaleContextHolder.getLocale()))
                        .build())
                .toList();

        return ResponseUtil.error(fieldErrors, ErrorCode.REQUEST_INVALID_INPUT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResult<Void>> handleGeneralException(final Exception e) {
        log.error("Unexpected error occurred: {}", e.getMessage(), e);
        return ResponseUtil.error(null, ErrorCode.SERVER_ERROR);
    }
}
