package com.findmymeme.findpost.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class FindPostUpdateRequest {

    @NotBlank
    private final String title;
    @NotBlank
    private final String htmlContent;
    @NotBlank
    private final String content;
    private final List<Long> tags;

    @Builder
    public FindPostUpdateRequest(String title, String htmlContent, String content, List<Long> tags) {
        this.title = title;
        this.htmlContent = htmlContent;
        this.content = content;
        this.tags = tags;
    }
}
