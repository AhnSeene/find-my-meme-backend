package com.findmymeme.memepost.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemePostTagProjection {
    private Long memePostId;
    private String tagName;

    public MemePostTagProjection(Long memePostId, String tagName) {
        this.memePostId = memePostId;
        this.tagName = tagName;
    }
}
