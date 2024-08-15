package com.findmymeme.findpost.dto;

import com.findmymeme.findpost.domain.FindPostComment;
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
        this.owner = isOwner;
        this.createdAt = comment.getCreatedAt();
    }
}
