package com.findmymeme.findpost.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "'찾아줘' 게시판 댓글 작성 요청 DTO")
@Getter
@NoArgsConstructor
public class FindPostCommentAddRequest {

    @Schema(description = "댓글 내용 (순수 텍스트)", example = "이거 혹시 XX 밈 아닌가요?", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{content.notblank}")
    private String content;
    @Schema(description = "댓글 내용 (HTML 형식)", example = "<p>이거 혹시 <strong>XX 밈</strong> 아닌가요?</p>", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{htmlContent.notblank}")
    private String htmlContent;

    @Schema(description = "부모 댓글 ID (대댓글일 경우에만 사용)", example = "201")
    private Long parentCommentId;

    @Builder
    public FindPostCommentAddRequest(String content, String htmlContent, Long parentCommentId) {
        this.content = content;
        this.htmlContent = htmlContent;
        this.parentCommentId = parentCommentId;
    }
}
