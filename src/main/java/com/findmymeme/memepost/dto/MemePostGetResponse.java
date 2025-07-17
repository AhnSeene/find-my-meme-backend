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
    private Long downloadCount;
    private String username;
    private String userProfileImageUrl;
    private boolean owner;
    private Boolean isLiked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> tags;

    @Builder
    public MemePostGetResponse(Long id, String imageUrl, String extension,
                               int height, int weight, Long size, String originalFilename,
                               Long likeCount, Long viewCount, Long downloadCount,
                               String username, String userProfileImageUrl, boolean isLiked,
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
        this.downloadCount = downloadCount;
        this.username = username;
        this.userProfileImageUrl = userProfileImageUrl;
        this.isLiked = isLiked;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.tags = tags;

    }

    public static MemePostGetResponse from(MemePost memePost, boolean isOwner, boolean isLiked, String fileBaseUrl) {
        MemePostGetResponse response = new MemePostGetResponse();
        response.id = memePost.getId();
        response.imageUrl = fileBaseUrl + memePost.getImageUrl();
        response.userProfileImageUrl = fileBaseUrl + memePost.getUser().getProfileImageUrl();
        response.extension = memePost.getExtension().getValue();
        response.height = memePost.getResolution().getHeight();
        response.weight = memePost.getResolution().getWidth();
        response.size = memePost.getSize();
        response.originalFilename = memePost.getOriginalFilename();
        response.likeCount = memePost.getLikeCount();
        response.viewCount = memePost.getViewCount();
        response.downloadCount = memePost.getDownloadCount();
        response.username = memePost.getUser().getUsername();
        response.owner = isOwner;
        response.isLiked = isLiked;
        response.createdAt = memePost.getCreatedAt();
        response.updatedAt = memePost.getUpdatedAt();
        response.tags = memePost.getTagNames();
        return response;
    }
}
