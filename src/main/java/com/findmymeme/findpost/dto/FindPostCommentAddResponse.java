package com.findmymeme.findpost.dto;

import com.findmymeme.findpost.domain.FindPostComment;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
        this.parentCommentId = comment.getParentComment().getId();
        this.findPostId = comment.getFindPost().getId();
        this.htmlContent = getHtmlContent();
        this.username = comment.getUser().getUsername();
        this.createdAt = comment.getCreatedAt();
    }
}
