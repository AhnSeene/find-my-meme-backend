package com.findmymeme.findpost.dto;

import com.findmymeme.findpost.domain.FindPostComment;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@NoArgsConstructor
public class FindPostCommentGetResponse {

    private Long id;
    private Long parentCommentId;
    private Long findPostId;
    private String htmlContent;
    private String username;
    private Boolean selected;
    private boolean owner;
    private LocalDateTime createdAt;

    public FindPostCommentGetResponse(FindPostComment comment, boolean isOwner) {
        this.id = comment.getId();
        this.parentCommentId = Optional.ofNullable(comment.getParentComment())
                .map(FindPostComment::getId)
                .orElse(null);
        this.findPostId = comment.getFindPost().getId();
        this.htmlContent = comment.getHtmlContent();
        this.username = comment.getUser().getUsername();
        this.selected = comment.isSelected();
        this.owner = isOwner;
        this.createdAt = comment.getCreatedAt();
    }

    @Builder
    public FindPostCommentGetResponse(Long id, Long parentCommentId, Long findPostId, String htmlContent,
                                      String username, boolean selected, boolean owner, LocalDateTime createdAt) {
        this.id = id;
        this.parentCommentId = parentCommentId;
        this.findPostId = findPostId;
        this.htmlContent = htmlContent;
        this.username = username;
        this.selected = selected;
        this.owner = owner;
        this.createdAt = createdAt;
    }
}
