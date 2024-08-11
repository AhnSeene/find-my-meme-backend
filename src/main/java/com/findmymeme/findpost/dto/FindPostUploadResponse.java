package com.findmymeme.findpost.dto;

import com.findmymeme.findpost.domain.FindPost;
import com.findmymeme.findpost.domain.FindStatus;
import lombok.Getter;

@Getter
public class FindPostUploadResponse {

    private String title;
    private String content;
    private FindStatus status;

    public FindPostUploadResponse(FindPost findPost) {
        this.title = findPost.getTitle();
        this.content = findPost.getContent();
        this.status = findPost.getFindStatus();
    }
}
