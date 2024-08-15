package com.findmymeme.findpost.dto;

import com.findmymeme.findpost.domain.FindPostComment;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@NoArgsConstructor
public class FindPostCommentAddResponse {

    private Long id;
    private Long parentCommentId;
    private Long findPostId;
    private String htmlContent;
    private String username;
    private LocalDateTime createdAt;

    public FindPostCommentAddResponse(FindPostComment comment) {
        this.id = comment.getId();
        this.parentCommentId = Optional.ofNullable(comment.getParentComment())
                .map(FindPostComment::getId)
                .orElse(null);
        this.findPostId = comment.getFindPost().getId();
        this.htmlContent = comment.getHtmlContent();
        this.username = comment.getUser().getUsername();
        this.createdAt = comment.getCreatedAt();
    }

    @Builder
    public FindPostCommentAddResponse(Long id, Long parentCommentId, Long findPostId, String htmlContent, String username, LocalDateTime createdAt) {
        this.id = id;
        this.parentCommentId = parentCommentId;
        this.findPostId = findPostId;
        this.htmlContent = htmlContent;
        this.username = username;
        this.createdAt = createdAt;
    }
}
