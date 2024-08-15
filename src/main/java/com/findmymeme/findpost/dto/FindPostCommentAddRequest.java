package com.findmymeme.findpost.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FindPostCommentAddRequest {

    @NotBlank(message = "{content.notblank}")
    private String content;
    @NotBlank(message = "{htmlContent.notblank}")
    private String htmlContent;

    private Long parentCommentId;

    @Builder
    public FindPostCommentAddRequest(String content, String htmlContent, Long parentCommentId) {
        this.content = content;
        this.htmlContent = htmlContent;
        this.parentCommentId = parentCommentId;
    }
}
