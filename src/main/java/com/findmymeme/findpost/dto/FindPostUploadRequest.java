package com.findmymeme.findpost.dto;

import com.findmymeme.findpost.domain.FindStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
public class FindPostUploadRequest {

    private String title;
    private String htmlContent;
    private String content;

    @Builder
    public FindPostUploadRequest(String title, String htmlContent, String content) {
        this.title = title;
        this.htmlContent = htmlContent;
        this.content = content;
    }
}
