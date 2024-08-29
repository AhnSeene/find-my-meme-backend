package com.findmymeme.findpost.dto;

import com.findmymeme.findpost.domain.FindStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class FindPostUploadRequest {

    @NotBlank(message = "{title.notblank}")
    private String title;
    @NotBlank(message = "{htmlContent.notblank}")
    private String htmlContent;
    @NotBlank(message = "{content.notblank}")
    private String content;
    @NotNull
    private List<Long> tags;

    @Builder
    public FindPostUploadRequest(String title, String htmlContent, String content, List<Long> tags) {
        this.title = title;
        this.htmlContent = htmlContent;
        this.content = content;
        this.tags = tags;
    }
}
