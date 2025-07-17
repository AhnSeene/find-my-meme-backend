package com.findmymeme.findpost.dto;

import com.findmymeme.findpost.domain.FindPost;
import com.findmymeme.findpost.domain.FindStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "내가 쓴 '찾아줘' 게시글 목록 요약 정보 응답 DTO")
@Getter
public class MyFindPostSummaryResponse {

    @Schema(description = "게시글 ID", example = "101")
    private Long id;
    @Schema(description = "게시글 제목", example = "이 짤 원본 아시는 분?")
    private String title;
    @Schema(description = "게시글 내용 (순수 텍스트 미리보기)", example = "사진 속 이 짤 원본 찾습니다!")
    private String content;
    @Schema(description = "게시글 상태 (FIND: 찾는중, FOUND: 찾음)", example = "FIND")
    private FindStatus status;
    @Schema(description = "조회수", example = "128")
    private Long viewCount;
    @Schema(description = "댓글 수", example = "5")
    private int commentCount;
    @Schema(description = "생성 일시", example = "2023-10-27T10:00:00")
    private LocalDateTime createdAt;
    @Schema(description = "적용된 태그 이름 목록", example = "[\"질문\", \"고전\"]")
    private List<String> tags;

    public MyFindPostSummaryResponse(final FindPost findPost, List<String> tags) {
        this.id = findPost.getId();
        this.title = findPost.getTitle();
        this.content = findPost.getContent();
        this.status = findPost.getFindStatus();
        this.viewCount = findPost.getViewCount();
        this.commentCount = findPost.getCommentCount();
        this.createdAt = findPost.getCreatedAt();
        this.tags = tags;
    }

    @Builder
    public MyFindPostSummaryResponse(String title, String content, FindStatus status, Long viewCount,
                                     int commentCount, LocalDateTime createdAt, List<String> tags) {
        this.title = title;
        this.content = content;
        this.status = status;
        this.viewCount = viewCount;
        this.commentCount = commentCount;
        this.createdAt = createdAt;
        this.tags = tags;
    }
}
