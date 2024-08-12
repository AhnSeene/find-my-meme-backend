package com.findmymeme.findpost.dto;

import com.findmymeme.findpost.domain.FindPost;
import com.findmymeme.findpost.domain.FindStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FindPostGetResponse {

    private String title;
    private String content;
    private FindStatus status;
    private boolean owner;

    public FindPostGetResponse(FindPost findPost, boolean isOwner) {
        this.title = findPost.getTitle();
        this.content = findPost.getContent();
        this.status = findPost.getFindStatus();
        this.owner = isOwner;
    }
}
