package com.findmymeme.findpost.dto;

import com.findmymeme.findpost.domain.FindPostComment;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class FindPostCommentSummaryResponse {

    private Long id;
    private Long postId;
    private String htmlContent;
    private String username;
    private LocalDateTime createdAt;

    private List<ReplyResponse> replys = new ArrayList<>();

    @Builder
    public FindPostCommentSummaryResponse(Long id, Long postId, String htmlContent, String username, LocalDateTime createdAt) {
        this.id = id;
        this.postId = postId;
        this.htmlContent = htmlContent;
        this.username = username;
        this.createdAt = createdAt;
    }

    public FindPostCommentSummaryResponse(FindPostComment comment) {
        this.id = comment.getId();
        this.postId = comment.getId();
        this.htmlContent = comment.getHtmlContent();
        this.username = comment.getUser().getUsername();
        this.createdAt = comment.getCreatedAt();
    }

    public void addReply(FindPostComment reply) {
        replys.add(new ReplyResponse(reply));
    }

    @Getter
    @NoArgsConstructor
    private static class ReplyResponse {
        private Long id;
        private Long postId;
        private Long parentCommentId;
        private String htmlContent;
        private String username;
        private LocalDateTime createdAt;

        @Builder
        public ReplyResponse(Long id, Long postId, Long parentCommentId, String htmlContent, String username, LocalDateTime createdAt) {
            this.id = id;
            this.postId = postId;
            this.parentCommentId = parentCommentId;
            this.htmlContent = htmlContent;
            this.username = username;
            this.createdAt = createdAt;
        }

        public ReplyResponse(FindPostComment comment) {
            this.id = comment.getId();
            this.postId = comment.getId();
            this.parentCommentId = comment.getParentComment().getId();
            this.htmlContent = comment.getHtmlContent();
            this.username = comment.getUser().getUsername();
            this.createdAt = comment.getCreatedAt();
        }
    }


}
