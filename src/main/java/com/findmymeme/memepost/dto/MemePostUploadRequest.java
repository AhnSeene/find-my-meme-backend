package com.findmymeme.memepost.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class MemePostUploadRequest {

    @NotBlank
    private String imageUrl;
    @NotEmpty(message = "{tags.notblank}")
    private List<Long> tags;

    @Builder
    public MemePostUploadRequest(String imageUrl, List<Long> tags) {
        this.imageUrl = imageUrl;
        this.tags = tags;
    }
}
