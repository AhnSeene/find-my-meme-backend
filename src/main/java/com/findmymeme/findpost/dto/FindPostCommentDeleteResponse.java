package com.findmymeme.findpost.dto;

import com.findmymeme.findpost.domain.FindPostComment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Optional;

@Schema(description = "'찾아줘' 게시판 댓글 삭제 응답 DTO")
@Getter
@NoArgsConstructor
public class FindPostCommentDeleteResponse {

    private static final String DELETED_COMMENT = "삭제된 댓글입니다.";
    @Schema(description = "삭제된 댓글 ID", example = "205")
    private Long id;
    @Schema(description = "부모 댓글 ID (대댓글이었던 경우)", example = "201")
    private Long parentCommentId;
    @Schema(description = "댓글이 달렸던 게시글 ID", example = "101")
    private Long findPostId;
    @Schema(description = "댓글 내용. 항상 '삭제된 댓글입니다.'를 반환합니다.", example = "삭제된 댓글입니다.")
    private String htmlContent;
    @Schema(description = "작성자 닉네임", example = "meme_finder")
    private String username;
    @Schema(description = "채택된 댓글이었는지 여부", example = "false")
    private Boolean selected;
    @Schema(description = "요청한 사용자가 댓글의 소유주였는지 여부", example = "true")
    private boolean owner;
    @Schema(description = "생성 일시", example = "2023-10-27T10:15:00")
    private LocalDateTime createdAt;
    @Schema(description = "삭제 일시 (항상 현재 시간과 가까움)", example = "2023-10-27T11:00:00")
    private LocalDateTime deletedAt;

    public FindPostCommentDeleteResponse(FindPostComment comment, boolean isOwner) {
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
    public FindPostCommentDeleteResponse(Long id, Long parentCommentId, Long findPostId, String htmlContent,
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
