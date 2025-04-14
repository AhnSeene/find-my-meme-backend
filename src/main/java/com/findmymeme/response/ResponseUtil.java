package com.findmymeme.response;

import com.findmymeme.exception.ErrorCode;
import org.springframework.http.ResponseEntity;

public class ResponseUtil {

    public static <T> ResponseEntity<ApiResponse<T>> success(T data, SuccessCode successCode) {
        ApiResponse<T> response = ApiResponse.success(successCode.getMessage(), data);
        return new ResponseEntity<>(response, successCode.getStatus());
    }

    public static <T> ResponseEntity<ApiResponse<T>> error(T data, ErrorCode errorCode) {
        ApiResponse<T> response = ApiResponse.error(errorCode, data);
        return new ResponseEntity<>(response, errorCode.getHttpStatus());
    }
}
