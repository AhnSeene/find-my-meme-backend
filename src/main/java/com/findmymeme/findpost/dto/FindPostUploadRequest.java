package com.findmymeme.findpost.dto;

import com.findmymeme.findpost.domain.FindStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
public class FindPostUploadRequest {

    private String title;
    private String content;
    private FindStatus status;

    @Builder
    public FindPostUploadRequest(String title, String content, FindStatus status) {
        this.title = title;
        this.content = content;
        this.status = status;
    }
}
