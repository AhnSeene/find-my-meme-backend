package com.findmymeme.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class FieldErrorDto {

    private String field;
    private String value;
    private String reason;

    @Override
    public String toString() {
        return "FieldErrorDto{" +
                "field='" + field + '\'' +
                ", value='" + value + '\'' +
                ", reason='" + reason + '\'' +
                '}';
    }
}
