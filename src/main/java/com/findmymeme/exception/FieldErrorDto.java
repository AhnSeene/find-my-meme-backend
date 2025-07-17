package com.findmymeme.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "유효성 검사 필드 에러 정보 DTO")
@Getter
@Builder
@AllArgsConstructor
public class FieldErrorDto {

    @Schema(description = "오류가 발생한 필드명", example = "username")
    private String field;
    @Schema(description = "사용자가 입력한 값", example = "user!")
    private String value;
    @Schema(description = "오류 원인", example = "아이디는 영문 소문자, 숫자로만 이루어져야 합니다.")
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
