package com.findmymeme.findpost.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class FindPostUploadRequest {

    @NotBlank(message = "{title.notblank}")
    private String title;
    @NotBlank(message = "{htmlContent.notblank}")
    private String htmlContent;
    @NotBlank(message = "{content.notblank}")
    private String content;

    private List<Long> tags;

    @Builder
    public FindPostUploadRequest(String title, String htmlContent, String content, List<Long> tags) {
        this.title = title;
        this.htmlContent = htmlContent;
        this.content = content;
        this.tags = tags;
        if (tags == null) {
            this.tags = new ArrayList<>();
        }
    }
}
