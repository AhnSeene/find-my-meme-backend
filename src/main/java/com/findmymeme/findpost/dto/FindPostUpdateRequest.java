package com.findmymeme.findpost.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class FindPostUpdateRequest {

    @NotBlank
    private String title;
    @NotBlank
    private String htmlContent;
    @NotBlank
    private String content;
    private List<Long> tags;

    @Builder
    public FindPostUpdateRequest(String title, String htmlContent, String content, List<Long> tags) {
        this.title = title;
        this.htmlContent = htmlContent;
        this.content = content;
        this.tags = tags;
        if (tags == null) {
            this.tags = new ArrayList<>();
        }
    }
}
