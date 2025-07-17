package com.findmymeme.findpost.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "'찾아줘' 게시글 작성 요청 DTO")
@Getter
public class FindPostUploadRequest {

    @Schema(description = "게시글 제목", example = "이 짤 원본 아시는 분?", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{title.notblank}")
    private String title;
    @Schema(description = "게시글 내용 (HTML 형식)", example = "<p>사진 속 이 짤 원본 찾습니다!</p><img src='...'>", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{htmlContent.notblank}")
    private String htmlContent;
    @Schema(description = "게시글 내용 (순수 텍스트 형식)", example = "사진 속 이 짤 원본 찾습니다!", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{content.notblank}")
    private String content;

    @Schema(description = "게시물에 적용할 태그 ID 목록", example = "[1, 5, 12]")
    private List<Long> tags;

    @Builder
    public FindPostUploadRequest(String title, String htmlContent, String content, List<Long> tags) {
        this.title = title;
        this.htmlContent = htmlContent;
        this.content = content;
        this.tags = tags;
        if (tags == null) {
            this.tags = new ArrayList<>();
        }
    }
}
