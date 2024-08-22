package com.findmymeme.findpost.dto;

import com.findmymeme.findpost.domain.FindPost;
import com.findmymeme.findpost.domain.FindStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class MyFindPostSummaryResponse {

    private Long id;
    private String title;
    private String content;
    private FindStatus status;
    private Long viewCount;
    private int commentCount;
    private LocalDateTime createdAt;
    private List<String> tags;

    public MyFindPostSummaryResponse(final FindPost findPost, List<String> tags) {
        this.id = findPost.getId();
        this.title = findPost.getTitle();
        this.content = findPost.getContent();
        this.status = findPost.getFindStatus();
        this.viewCount = findPost.getViewCount();
        this.commentCount = findPost.getCommentCount();
        this.createdAt = findPost.getCreatedAt();
        this.tags = tags;
    }

    @Builder
    public MyFindPostSummaryResponse(String title, String content, FindStatus status, Long viewCount,
                                     int commentCount, LocalDateTime createdAt, List<String> tags) {
        this.title = title;
        this.content = content;
        this.status = status;
        this.viewCount = viewCount;
        this.commentCount = commentCount;
        this.createdAt = createdAt;
        this.tags = tags;
    }
}
