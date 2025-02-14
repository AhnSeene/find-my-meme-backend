package com.findmymeme.file.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PresignedUploadResponse {
    private String presignedUrl;

    public PresignedUploadResponse(String presignedUrl) {
        this.presignedUrl = presignedUrl;
    }
}
