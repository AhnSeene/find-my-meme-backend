package com.findmymeme.memepost.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "밈 게시물 좋아요 토글 응답 DTO")
@Getter
@NoArgsConstructor
public class MemePostLikeResponse {
    @Schema(description = "좋아요 토글 후의 상태. true이면 '좋아요' 상태, false이면 '좋아요 취소' 상태.", example = "true")
    private Boolean isLiked;

    public MemePostLikeResponse(boolean isLiked) {
        this.isLiked = isLiked;
    }
}
