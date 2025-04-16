package com.findmymeme.memepost.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.core.io.Resource;

@NoArgsConstructor
@Getter
public class MemePostDownloadDto {
    private String filename;
    private String presignedUrl;

    @Builder
    public MemePostDownloadDto(String filename, String presignedUrl) {
        this.filename = filename;
        this.presignedUrl = presignedUrl;
    }
}
