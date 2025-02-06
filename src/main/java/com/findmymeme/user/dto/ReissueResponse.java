package com.findmymeme.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReissueResponse {

    private String accessToken;

    public ReissueResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}
