package com.findmymeme.response;

import com.findmymeme.exception.ErrorCode;
import org.springframework.http.ResponseEntity;

public class ResponseUtil {

    public static <T> ResponseEntity<ApiResult<T>> success(T data, SuccessCode successCode) {
        ApiResult<T> response = ApiResult.success(successCode.getMessage(), data);
        return new ResponseEntity<>(response, successCode.getStatus());
    }

    public static <T> ResponseEntity<ApiResult<T>> error(T data, ErrorCode errorCode) {
        ApiResult<T> response = ApiResult.error(errorCode, data);
        return new ResponseEntity<>(response, errorCode.getHttpStatus());
    }
}
