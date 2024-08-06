package com.findmymeme.exception;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {

    private HttpStatus status;
    private String message;

    @Builder
    public ErrorResponse(final HttpStatus status, final String message) {
        this.status = status;
        this.message = message;
    }
}
