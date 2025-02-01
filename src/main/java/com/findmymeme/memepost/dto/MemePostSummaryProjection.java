package com.findmymeme.memepost.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemePostSummaryProjection {

    private Long id;
    private String imageUrl;
    private Long likeCount;
    private Long viewCount;
    private Long downloadCount;

    @Builder
    public MemePostSummaryProjection(Long id, String imageUrl, Long likeCount, Long viewCount, Long downloadCount) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.likeCount = likeCount;
        this.viewCount = viewCount;
        this.downloadCount = downloadCount;
    }
}
