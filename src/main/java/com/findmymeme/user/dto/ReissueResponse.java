package com.findmymeme.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "토큰 재발급 응답 DTO")
@Getter
@NoArgsConstructor
public class ReissueResponse {

    @Schema(description = "새로 발급된 Access Token", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWI...")
    private String accessToken;

    public ReissueResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}
