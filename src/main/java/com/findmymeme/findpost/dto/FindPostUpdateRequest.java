package com.findmymeme.findpost.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
public class FindPostUpdateRequest {

    @NotBlank
    private final String title;
    @NotBlank
    private final String htmlContent;
    @NotBlank
    private final String content;

    @Builder
    public FindPostUpdateRequest(String title, String htmlContent, String content) {
        this.title = title;
        this.htmlContent = htmlContent;
        this.content = content;
    }
}
