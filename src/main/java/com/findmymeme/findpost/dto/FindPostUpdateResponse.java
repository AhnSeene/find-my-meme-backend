package com.findmymeme.findpost.dto;

import com.findmymeme.findpost.domain.FindPost;
import com.findmymeme.findpost.domain.FindStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FindPostUpdateResponse {

    private Long id;
    private String title;
    private String content;
    private FindStatus status;

    public FindPostUpdateResponse(FindPost findPost) {
        this.id = findPost.getId();
        this.title = findPost.getTitle();
        this.content = findPost.getContent();
        this.status = findPost.getFindStatus();
    }

    @Builder
    public FindPostUpdateResponse(Long id, String title, String content, FindStatus status) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.status = status;
    }
}
