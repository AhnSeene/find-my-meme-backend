package com.findmymeme.findpost.dto;

import com.findmymeme.findpost.domain.FindPostComment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Schema(description = "'찾아줘' 게시판 댓글 목록 요약 정보 응답 DTO (계층 구조 포함)")
@Getter
@NoArgsConstructor
public class FindPostCommentSummaryResponse {


    private static final String DELETED_COMMENT = "삭제된 댓글입니다.";

    @Schema(description = "댓글 ID", example = "201")
    private Long id;
    @Schema(description = "게시글 ID", example = "101")
    private Long postId;
    @Schema(description = "부모 댓글 ID (최상위 댓글은 null)", example = "null")
    private Long parentCommentId;
    @Schema(description = "댓글 내용 (HTML). 삭제된 경우 '삭제된 댓글입니다.' 반환", example = "<p>이거 혹시 <strong>XX 밈</strong> 아닌가요?</p>")
    private String htmlContent;
    @Schema(description = "작성자 닉네임", example = "meme_finder")
    private String username;
    @Schema(description = "채택된 댓글인지 여부", example = "false")
    private Boolean selected;
    @Schema(description = "생성 일시", example = "2023-10-27T10:15:00")
    private LocalDateTime createdAt;
    @Schema(description = "삭제 일시 (삭제된 경우)", example = "null")
    private LocalDateTime deletedAt;

    @Schema(description = "이 댓글에 달린 대댓글 목록")
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
