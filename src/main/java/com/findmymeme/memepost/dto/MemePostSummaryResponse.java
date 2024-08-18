package com.findmymeme.memepost.dto;

import com.findmymeme.memepost.domain.MemePost;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class MemePostSummaryResponse {

    private Long id;
    private String imageUrl;
    private Long likeCount;
    private Long viewCount;
    private List<String> tags;

    @Builder
    public MemePostSummaryResponse(Long id, String imageUrl, Long likeCount, Long viewCount, List<String> tags) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.likeCount = likeCount;
        this.viewCount = viewCount;
        this.tags = tags;
    }

    public MemePostSummaryResponse(MemePost memePost, List<String> tags) {
        this.id = memePost.getId();
        this.imageUrl = memePost.getImageUrl();
        this.likeCount = memePost.getLikeCount();
        this.viewCount = memePost.getViewCount();
        this.tags = tags;
    }
}
