package com.findmymeme.findpost.dto;

import com.findmymeme.findpost.domain.FindPost;
import com.findmymeme.findpost.domain.FindStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FindPostUploadResponse {

    private Long id;
    private String title;
    private String content;
    private FindStatus status;

    public FindPostUploadResponse(FindPost findPost) {
        this.id = findPost.getId();
        this.title = findPost.getTitle();
        this.content = findPost.getContent();
        this.status = findPost.getFindStatus();
    }

    @Builder
    public FindPostUploadResponse(Long id, String title, String content, FindStatus status) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.status = status;
    }
}
