package com.findmymeme.findpost.dto;

import com.findmymeme.findpost.domain.FindPost;
import com.findmymeme.findpost.domain.FindStatus;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class FindPostUpdateResponse {

    private Long id;
    private String title;
    private String content;
    private FindStatus status;
    private List<String> tags;

    public FindPostUpdateResponse(FindPost findPost, List<String> tags) {
        this.id = findPost.getId();
        this.title = findPost.getTitle();
        this.content = findPost.getContent();
        this.status = findPost.getFindStatus();
        this.tags = tags;
    }

    @Builder
    public FindPostUpdateResponse(Long id, String title, String content, FindStatus status, List<String> tags) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.status = status;
        this.tags = tags;
    }
}
