package com.findmymeme.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReissueTokenResponse {
    private String accessToken;
    private String refreshToken;
}
