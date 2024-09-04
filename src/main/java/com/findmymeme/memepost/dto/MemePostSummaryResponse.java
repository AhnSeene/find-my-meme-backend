package com.findmymeme.memepost.dto;

import com.findmymeme.memepost.domain.MemePost;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MemePostSummaryResponse {

    private Long id;
    private String imageUrl;
    private Long likeCount;
    private Long viewCount;
    private Long downloadCount;
    private Boolean isLiked;
    private List<String> tags;

    @Builder
    public MemePostSummaryResponse(Long id, String imageUrl, Long likeCount, Long viewCount, Long downloadCount, boolean isLiked, List<String> tags) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.likeCount = likeCount;
        this.viewCount = viewCount;
        this.downloadCount = downloadCount;
        this.isLiked = isLiked;
        this.tags = tags;
    }

    public MemePostSummaryResponse(MemePost memePost, boolean isLiked, List<String> tags) {
        this.id = memePost.getId();
        this.imageUrl = memePost.getImageUrl();
        this.likeCount = memePost.getLikeCount();
        this.viewCount = memePost.getViewCount();
        this.downloadCount = memePost.getDownloadCount();
        this.isLiked = isLiked;
        this.tags = tags;
    }

    public MemePostSummaryResponse(MemePost memePost, boolean isLiked) {
        this.id = memePost.getId();
        this.imageUrl = memePost.getImageUrl();
        this.likeCount = memePost.getLikeCount();
        this.viewCount = memePost.getViewCount();
        this.downloadCount = memePost.getDownloadCount();
        this.isLiked = isLiked;
    }
}
