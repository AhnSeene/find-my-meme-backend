package com.findmymeme.findpost.dto;

import com.findmymeme.findpost.domain.FindPostComment;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@NoArgsConstructor
public class FindPostCommentSummaryResponse {


    private static final String DELETED_COMMENT = "삭제된 댓글입니다.";

    private Long id;
    private Long postId;
    private Long parentCommentId;
    private String htmlContent;
    private String username;
    private Boolean selected;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;

    private List<FindPostCommentSummaryResponse> replies = new ArrayList<>();

    @Builder
    public FindPostCommentSummaryResponse(Long id, Long postId, Long parentCommentId, String htmlContent,
                                          String username, Boolean selected, LocalDateTime createdAt,  LocalDateTime deletedAt,
                                          List<FindPostCommentSummaryResponse> replies) {
        this.id = id;
        this.postId = postId;
        this.parentCommentId = parentCommentId;
        this.htmlContent = htmlContent;
        this.username = username;
        this.selected = selected;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
        this.replies = replies;
    }

    public FindPostCommentSummaryResponse(FindPostComment comment) {
        this.id = comment.getId();
        this.postId = comment.getFindPost().getId();
        this.parentCommentId = Optional.ofNullable(comment.getParentComment())
                .map(FindPostComment::getId)
                .orElse(null);
        this.username = comment.getUser().getUsername();
        this.selected = comment.isSelected();
        this.createdAt = comment.getCreatedAt();
        this.htmlContent = comment.isDeleted() ? DELETED_COMMENT : comment.getHtmlContent();
        this.deletedAt = comment.getDeletedAt();
    }

    public void addReply(FindPostCommentSummaryResponse reply) {
        replies.add(reply);
    }

//    @Getter
//    @NoArgsConstructor
//    private static class ReplyResponse {
//        private Long id;
//        private Long postId;
//        private Long parentCommentId;
//        private String sendTo;
//        private String htmlContent;
//        private String username;
//        private LocalDateTime createdAt;
//
//        @Builder
//        public ReplyResponse(Long id, Long postId, Long parentCommentId, String sendTo, String htmlContent, String username, LocalDateTime createdAt) {
//            this.id = id;
//            this.postId = postId;
//            this.parentCommentId = parentCommentId;
//            this.sendTo = sendTo;
//            this.htmlContent = htmlContent;
//            this.username = username;
//            this.createdAt = createdAt;
//        }
//
//        public ReplyResponse(FindPostComment comment) {
//            this.id = comment.getId();
//            this.postId = comment.getId();
//            this.parentCommentId = Optional.ofNullable(comment.getParentComment())
//                    .map(FindPostComment::getId)
//                    .orElse(null);
//
//            this.htmlContent = comment.getHtmlContent();
//            this.username = comment.getUser().getUsername();
//            this.createdAt = comment.getCreatedAt();
//        }
//    }


}
