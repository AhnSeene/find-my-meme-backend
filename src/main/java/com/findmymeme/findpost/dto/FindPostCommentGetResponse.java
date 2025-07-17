package com.findmymeme.findpost.dto;

import com.findmymeme.findpost.domain.FindPostComment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Optional;

@Schema(description = "'찾아줘' 게시판 댓글 단건 조회 응답 DTO")
@Getter
@NoArgsConstructor
public class FindPostCommentGetResponse {

    private static final String DELETED_COMMENT = "삭제된 댓글입니다.";

    @Schema(description = "댓글 ID", example = "205")
    private Long id;
    @Schema(description = "부모 댓글 ID (대댓글인 경우)", example = "201")
    private Long parentCommentId;
    @Schema(description = "댓글이 달린 게시글 ID", example = "101")
    private Long findPostId;
    @Schema(description = "댓글 내용 (HTML 형식). 삭제된 경우 '삭제된 댓글입니다.' 반환", example = "<p>이거 혹시 <strong>XX 밈</strong> 아닌가요?</p>")
    private String htmlContent;
    @Schema(description = "작성자 닉네임", example = "meme_finder")
    private String username;
    @Schema(description = "게시물 작성자에 의해 채택된 댓글인지 여부", example = "false")
    private Boolean selected;
    @Schema(description = "요청한 사용자가 댓글의 소유주인지 여부", example = "true")
    private boolean owner;
    @Schema(description = "생성 일시", example = "2023-10-27T10:15:00")
    private LocalDateTime createdAt;
    @Schema(description = "삭제 일시 (삭제된 경우)", example = "null")
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
