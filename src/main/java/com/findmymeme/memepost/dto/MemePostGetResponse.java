package com.findmymeme.memepost.dto;

import com.findmymeme.memepost.domain.MemePost;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class MemePostGetResponse {

    private Long id;
    private String imageUrl;
    private String extension;
    private int height;
    private int weight;
    private Long size;
    private String originalFilename;
    private Long likeCount;
    private Long viewCount;
    private String username;
    private boolean owner;
    private Boolean isLiked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> tags;

    @Builder
    public MemePostGetResponse(Long id, String imageUrl, String extension,
                               int height, int weight, Long size, String originalFilename,
                               Long likeCount, Long viewCount, String username, boolean isLiked,
                               LocalDateTime createdAt, LocalDateTime updatedAt, List<String> tags) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.extension = extension;
        this.height = height;
        this.weight = weight;
        this.size = size;
        this.originalFilename = originalFilename;
        this.likeCount = likeCount;
        this.viewCount = viewCount;
        this.username = username;
        this.isLiked = isLiked;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.tags = tags;

    }

    public MemePostGetResponse(MemePost memePost, boolean isOwner, boolean isLiked, List<String> tags) {
        this.id = memePost.getId();
        this.imageUrl = memePost.getImageUrl();
        this.extension = memePost.getExtension();
        this.height = memePost.getResolution().getHeight();
        this.weight = memePost.getResolution().getWidth();
        this.size = memePost.getSize();
        this.originalFilename = memePost.getOriginalFilename();
        this.likeCount = memePost.getLikeCount();
        this.viewCount = memePost.getViewCount();
        this.username = memePost.getUser().getUsername();
        this.owner = isOwner;
        this.isLiked = isLiked;
        this.createdAt = memePost.getCreatedAt();
        this.updatedAt = memePost.getUpdatedAt();
        this.tags = tags;
    }
}
