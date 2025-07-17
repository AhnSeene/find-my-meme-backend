package com.findmymeme.findpost.dto;

import com.findmymeme.common.dto.TagResponse;
import com.findmymeme.findpost.domain.FindPost;
import com.findmymeme.findpost.domain.FindStatus;
import com.findmymeme.tag.domain.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "'찾아줘' 게시글 상세 조회 응답 DTO")
@Getter
public class FindPostGetResponse {

    @Schema(description = "게시글 ID", example = "101")
    private Long id;
    @Schema(description = "게시글 제목", example = "이 짤 원본 아시는 분?")
    private String title;
    @Schema(description = "게시글 내용 (HTML 형식)", example = "<p>사진 속 이 짤 원본 찾습니다!</p>")
    private String htmlContent;
    @Schema(description = "게시글 상태 (FIND: 찾는중, FOUND: 찾음)", example = "FIND")
    private FindStatus status;
    @Schema(description = "작성자 닉네임", example = "meme_king")
    private String username;
    @Schema(description = "요청한 사용자가 게시글의 소유주인지 여부", example = "true")
    private boolean owner;
    @Schema(description = "게시글에 달린 태그 목록")
    private List<TagResponse> tags;
    @Schema(description = "조회수", example = "128")
    private Long viewCount;
    @Schema(description = "댓글 수", example = "5")
    private int commentCount;
    @Schema(description = "생성 일시", example = "2023-10-27T10:00:00")
    private LocalDateTime createdAt;
    @Schema(description = "최종 수정 일시", example = "2023-10-27T11:30:00")
    private LocalDateTime updatedAt;

    public FindPostGetResponse(FindPost findPost, boolean isOwner, List<Tag> tags) {
        this.id = findPost.getId();
        this.title = findPost.getTitle();
        this.htmlContent = findPost.getHtmlContent();
        this.status = findPost.getFindStatus();
        this.username = findPost.getUser().getUsername();
        this.owner = isOwner;
        this.tags = tags.stream()
                .map(TagResponse::new)
                .toList();
        this.viewCount = findPost.getViewCount();
        this.commentCount = findPost.getCommentCount();
        this.createdAt = findPost.getCreatedAt();
        this.updatedAt = findPost.getUpdatedAt();
    }

    @Builder
    public FindPostGetResponse(String title, String htmlContent, FindStatus status,
                               String username, boolean owner, List<TagResponse> tags, Long viewCount,
                               int commentCount, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.title = title;
        this.htmlContent = htmlContent;
        this.status = status;
        this.username = username;
        this.owner = owner;
        this.tags = tags;
        this.viewCount = viewCount;
        this.commentCount = commentCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
