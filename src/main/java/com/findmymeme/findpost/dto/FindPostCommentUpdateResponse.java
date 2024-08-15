package com.findmymeme.findpost.dto;

import com.findmymeme.findpost.domain.FindPostComment;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FindPostCommentUpdateResponse {

    private Long id;
    private String content;

    public FindPostCommentUpdateResponse(FindPostComment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
    }

    @Builder
    public FindPostCommentUpdateResponse(Long id, String content) {
        this.id = id;
        this.content = content;
    }
}
