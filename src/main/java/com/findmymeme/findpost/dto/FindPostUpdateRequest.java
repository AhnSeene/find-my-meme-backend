package com.findmymeme.findpost.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "'찾아줘' 게시글 수정 요청 DTO")
@Getter
public class FindPostUpdateRequest {

    @Schema(description = "수정할 게시글 제목", example = "이 짤 원본 아시는 분? (수정)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String title;
    @Schema(description = "수정할 게시글 내용 (HTML 형식)", example = "<p>내용을 조금 추가합니다.</p>", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String htmlContent;
    @Schema(description = "수정할 게시글 내용 (순수 텍스트 형식)", example = "내용을 조금 추가합니다.", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String content;
    @Schema(description = "수정할 태그 ID 목록. 기존 태그는 모두 삭제되고 이 목록으로 대체됩니다.", example = "[1, 8]")
    private List<Long> tags;

    @Builder
    public FindPostUpdateRequest(String title, String htmlContent, String content, List<Long> tags) {
        this.title = title;
        this.htmlContent = htmlContent;
        this.content = content;
        this.tags = tags;
        if (tags == null) {
            this.tags = new ArrayList<>();
        }
    }
}
