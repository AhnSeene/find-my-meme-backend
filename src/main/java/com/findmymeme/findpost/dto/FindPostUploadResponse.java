package com.findmymeme.findpost.dto;

import com.findmymeme.findpost.domain.FindPost;
import com.findmymeme.findpost.domain.FindStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Schema(description = "'찾아줘' 게시글 작성 응답 DTO")
@Getter
@Builder
public class FindPostUploadResponse {

    @Schema(description = "생성된 게시글의 ID", example = "101")
    private Long id;
    @Schema(description = "게시글 제목", example = "이 짤 원본 아시는 분?")
    private String title;
    @Schema(description = "게시글 내용 (순수 텍스트)", example = "사진 속 이 짤 원본 찾습니다!")
    private String content;
    @Schema(description = "게시글 상태", example = "FIND")
    private FindStatus status;
    @Schema(description = "적용된 태그 이름 목록", example = "[\"질문\", \"고전\"]")
    private List<String> tags;

    public FindPostUploadResponse(FindPost findPost, List<String> tags) {
        this.id = findPost.getId();
        this.title = findPost.getTitle();
        this.content = findPost.getContent();
        this.status = findPost.getFindStatus();
        this.tags = tags;
    }

    @Builder
    public FindPostUploadResponse(Long id, String title, String content, FindStatus status, List<String> tags) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.status = status;
        this.tags = tags;
    }
}
