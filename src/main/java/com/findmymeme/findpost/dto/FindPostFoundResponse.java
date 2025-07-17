package com.findmymeme.findpost.dto;

import com.findmymeme.findpost.domain.FindPost;
import com.findmymeme.findpost.domain.FindStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "댓글 채택 응답 DTO")
@Getter
@NoArgsConstructor
public class FindPostFoundResponse {
    @Schema(description = "변경된 게시글 상태. 항상 'FOUND' 입니다.", example = "FOUND")
    private FindStatus findStatus;
    @Schema(description = "채택된 댓글의 ID", example = "205")
    private Long selectedCommentId;

    @Builder
    public FindPostFoundResponse(FindStatus findStatus, Long selectedCommentId) {
        this.findStatus = findStatus;
        this.selectedCommentId = selectedCommentId;
    }

    public FindPostFoundResponse(FindPost findPost) {
        this.findStatus = findPost.getFindStatus();
        this.selectedCommentId = findPost.getSelectedComment().getId();
    }
}
