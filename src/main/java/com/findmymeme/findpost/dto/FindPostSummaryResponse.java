package com.findmymeme.findpost.dto;

import com.findmymeme.findpost.domain.FindPost;
import com.findmymeme.findpost.domain.FindStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

@Getter
public class FindPostSummaryResponse {

    private String title;
    private String content;
    private FindStatus status;
    private String username;
    private LocalDateTime createdAt;

    public FindPostSummaryResponse(final FindPost findPost) {
        this.title = findPost.getTitle();
        this.content = findPost.getContent();
        this.status = findPost.getFindStatus();
        this.username = findPost.getUser().getUsername();
        this.createdAt = findPost.getCreatedAt();
    }

    @Builder
    public FindPostSummaryResponse(String title, String content, FindStatus status, String username, LocalDateTime createdAt) {
        this.title = title;
        this.content = content;
        this.status = status;
        this.username = username;
        this.createdAt = createdAt;
    }
}
