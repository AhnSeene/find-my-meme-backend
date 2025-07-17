package com.findmymeme.findpost.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "'찾아줘' 게시판 댓글 수정 요청 DTO")
@Getter
@NoArgsConstructor
public class FindPostCommentUpdateRequest {

    @Schema(description = "수정할 댓글 내용 (순수 텍스트)", example = "아, YY 밈이었네요. 정정합니다.", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{content.notblank}")
    private String content;
    @Schema(description = "수정할 댓글 내용 (HTML 형식)", example = "<p>아, <strong>YY 밈</strong>이었네요. 정정합니다.</p>", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{htmlContent.notblank}")
    private String htmlContent;


    @Builder
    public FindPostCommentUpdateRequest(String htmlContent, String content) {
        this.htmlContent = htmlContent;
        this.content = content;
    }
}
