package com.findmymeme.file.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PresignedUploadResponse {
    private String presinedUrl;

    public PresignedUploadResponse(String presinedUrl) {
        this.presinedUrl = presinedUrl;
    }
}
