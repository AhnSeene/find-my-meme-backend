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

    private static final String DELETED_COMMENT = "삭제된 댓글입니다.";

    private Long id;
    private Long parentCommentId;
    private Long findPostId;
    private String htmlContent;
    private String username;
    private Boolean selected;
    private boolean owner;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;

    public FindPostCommentGetResponse(FindPostComment comment, boolean isOwner) {
        this.id = comment.getId();
        this.parentCommentId = Optional.ofNullable(comment.getParentComment())
                .map(FindPostComment::getId)
                .orElse(null);
        this.findPostId = comment.getFindPost().getId();
        this.htmlContent = comment.isDeleted() ? DELETED_COMMENT : comment.getHtmlContent();
        this.username = comment.getUser().getUsername();
        this.selected = comment.isSelected();
        this.owner = isOwner;
        this.createdAt = comment.getCreatedAt();
        this.deletedAt = comment.getDeletedAt();
    }

    @Builder
    public FindPostCommentGetResponse(Long id, Long parentCommentId, Long findPostId, String htmlContent,
                                      String username, boolean selected, boolean owner, LocalDateTime createdAt,
                                      LocalDateTime deletedAt) {
        this.id = id;
        this.parentCommentId = parentCommentId;
        this.findPostId = findPostId;
        this.htmlContent = htmlContent;
        this.username = username;
        this.selected = selected;
        this.owner = owner;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
    }
}
