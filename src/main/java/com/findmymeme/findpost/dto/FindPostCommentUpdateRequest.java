package com.findmymeme.findpost.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
public class FindPostCommentUpdateRequest {

    @NotBlank
    private final String htmlContent;
    @NotBlank
    private final String content;

    @Builder
    public FindPostCommentUpdateRequest(String htmlContent, String content) {
        this.htmlContent = htmlContent;
        this.content = content;
    }
}
