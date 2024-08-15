package com.findmymeme.findpost.dto;

import com.findmymeme.findpost.domain.FindPostComment;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
        this.parentCommentId = comment.getParentComment().getId();
        this.findPostId = comment.getFindPost().getId();
        this.htmlContent = getHtmlContent();
        this.username = comment.getUser().getUsername();
        this.owner = isOwner;
        this.createdAt = comment.getCreatedAt();
    }
}
