package com.findmymeme.findpost.dto;

import com.findmymeme.findpost.domain.FindPost;
import com.findmymeme.findpost.domain.FindStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FindPostGetResponse {

    private String title;
    private String htmlContent;
    private FindStatus status;
    private boolean owner;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public FindPostGetResponse(FindPost findPost, boolean isOwner) {
        this.title = findPost.getTitle();
        this.htmlContent = findPost.getContent();
        this.status = findPost.getFindStatus();
        this.owner = isOwner;
        this.createdAt = findPost.getCreatedAt();
        this.updatedAt = findPost.getUpdatedAt();
    }
}
