package com.findmymeme.findpost.dto;

import com.findmymeme.findpost.domain.FindStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
public class FindPostUploadRequest {

    @NotBlank(message = "{title.notblank}")
    private String title;
    @NotBlank(message = "{htmlContent.notblank}")
    private String htmlContent;
    @NotBlank(message = "{content.notblank}")
    private String content;

    @Builder
    public FindPostUploadRequest(String title, String htmlContent, String content) {
        this.title = title;
        this.htmlContent = htmlContent;
        this.content = content;
    }
}
