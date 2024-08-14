package com.findmymeme.findpost.dto;

import com.findmymeme.findpost.domain.FindPost;
import com.findmymeme.findpost.domain.FindStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
public class FindPostGetResponse {

    private String title;
    private String htmlContent;
    private FindStatus status;
    private String username;
    private boolean owner;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public FindPostGetResponse(FindPost findPost, boolean isOwner) {
        this.title = findPost.getTitle();
        this.htmlContent = findPost.getHtmlContent();
        this.status = findPost.getFindStatus();
        this.username = findPost.getUser().getUsername();
        this.owner = isOwner;
        this.createdAt = findPost.getCreatedAt();
        this.updatedAt = findPost.getUpdatedAt();
    }

    @Builder
    public FindPostGetResponse(String title, String htmlContent, FindStatus status, String username, boolean owner, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.title = title;
        this.htmlContent = htmlContent;
        this.status = status;
        this.username = username;
        this.owner = owner;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}