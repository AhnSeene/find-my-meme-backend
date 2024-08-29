package com.findmymeme.memepost.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemePostLikeResponse {
    private Boolean isLiked;

    public MemePostLikeResponse(boolean isLiked) {
        this.isLiked = isLiked;
    }
}
