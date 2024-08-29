package com.findmymeme.memepost.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class MemePostUploadResponse {

    private String imageUrl;
    private List<String> tags;

    @Builder
    public MemePostUploadResponse(String imageUrl, List<String> tags) {
        this.imageUrl = imageUrl;
        this.tags = tags;
    }
}
