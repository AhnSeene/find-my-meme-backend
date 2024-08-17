package com.findmymeme.findpost.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FindPostCommentUpdateRequest {

    @NotBlank(message = "{content.notblank}")
    private String content;
    @NotBlank(message = "{htmlContent.notblank}")
    private String htmlContent;


    @Builder
    public FindPostCommentUpdateRequest(String htmlContent, String content) {
        this.htmlContent = htmlContent;
        this.content = content;
    }
}
