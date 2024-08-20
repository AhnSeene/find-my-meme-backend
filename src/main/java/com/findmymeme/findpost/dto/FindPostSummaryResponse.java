package com.findmymeme.findpost.dto;

import com.findmymeme.findpost.domain.FindPost;
import com.findmymeme.findpost.domain.FindStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class FindPostSummaryResponse {

    private Long id;
    private String title;
    private String content;
    private FindStatus status;
    private String username;
    private Long viewCount;
    private LocalDateTime createdAt;
    private List<String> tags;

    public FindPostSummaryResponse(final FindPost findPost, List<String> tags) {
        this.id = findPost.getId();
        this.title = findPost.getTitle();
        this.content = findPost.getContent();
        this.status = findPost.getFindStatus();
        this.username = findPost.getUser().getUsername();
        this.viewCount = findPost.getViewCount();
        this.createdAt = findPost.getCreatedAt();
        this.tags = tags;
    }

    @Builder
    public FindPostSummaryResponse(String title, String content, FindStatus status, Long viewCount,
                                   String username, LocalDateTime createdAt, List<String> tags) {
        this.title = title;
        this.content = content;
        this.status = status;
        this.username = username;
        this.viewCount = viewCount;
        this.createdAt = createdAt;
        this.tags = tags;
    }
}
