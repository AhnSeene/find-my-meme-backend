package com.findmymeme.findpost.dto;

import com.findmymeme.findpost.domain.FindPost;
import com.findmymeme.findpost.domain.FindStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FindPostFoundResponse {
    private FindStatus findStatus;
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
