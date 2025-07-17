package com.findmymeme.findpost.dto;

import com.findmymeme.findpost.domain.FindPostComment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Optional;

@Schema(description = "'찾아줘' 게시판 댓글 수정 응답 DTO")
@Getter
public class FindPostCommentUpdateResponse {

    @Schema(description = "수정된 댓글 ID", example = "205")
    private Long id;
    @Schema(description = "부모 댓글 ID (대댓글인 경우)", example = "201")
    private Long parentCommentId;
    @Schema(description = "댓글이 달린 게시글 ID", example = "101")
    private Long findPostId;
    @Schema(description = "수정된 댓글 내용 (HTML 형식)", example = "<p>아, <strong>YY 밈</strong>이었네요. 정정합니다.</p>")
    private String htmlContent;
    @Schema(description = "작성자 닉네임", example = "meme_finder")
    private String username;
    @Schema(description = "생성 일시", example = "2023-10-27T10:15:00")
    private LocalDateTime createdAt;
    @Schema(description = "수정 일시", example = "2023-10-27T10:20:00")
    private LocalDateTime updatedAt;

    public FindPostCommentUpdateResponse(FindPostComment comment) {
        this.id = comment.getId();
        this.parentCommentId = Optional.ofNullable(comment.getParentComment())
                .map(FindPostComment::getId)
                .orElse(null);
        this.findPostId = comment.getFindPost().getId();
        this.htmlContent = comment.getHtmlContent();
        this.username = comment.getUser().getUsername();
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
    }

    @Builder
    public FindPostCommentUpdateResponse(Long id, Long parentCommentId, Long findPostId, String htmlContent, String username, LocalDateTime createdAt) {
        this.id = id;
        this.parentCommentId = parentCommentId;
        this.findPostId = findPostId;
        this.htmlContent = htmlContent;
        this.username = username;
        this.createdAt = createdAt;
    }
}
