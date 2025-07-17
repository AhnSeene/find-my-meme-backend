package com.findmymeme.memepost.dto;

import com.findmymeme.memepost.domain.MemePost;
import com.findmymeme.memepost.domain.ProcessingStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class MemePostSummaryResponse {

    private Long id;
    private String imageUrl;
    private String thumbnail288Url;
    private String thumbnail657Url;
    private ProcessingStatus processingStatus;
    private Long likeCount;
    private Long viewCount;
    private Long downloadCount;
    private Boolean isLiked = false;
    private List<String> tags;

    @Builder
    private MemePostSummaryResponse(Long id, String imageUrl, String thumbnail288Url, String thumbnail657Url,
                                   Long likeCount, Long viewCount, Long downloadCount, Boolean isLiked, List<String> tags,
                                    ProcessingStatus processingStatus) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.thumbnail288Url = thumbnail288Url;
        this.thumbnail657Url = thumbnail657Url;
        this.likeCount = likeCount;
        this.viewCount = viewCount;
        this.downloadCount = downloadCount;
        this.isLiked = isLiked;
        this.tags = tags;
        this.processingStatus = processingStatus;
    }


    public static MemePostSummaryResponse from(MemePost memePost, boolean isLiked, List<String> tags, String fileBaseUrl) {
        return MemePostSummaryResponse.builder()
                .id(memePost.getId())
                .imageUrl(fileBaseUrl + memePost.getImageUrl())
                .thumbnail288Url(generateFullUrl(fileBaseUrl, memePost.getThumbnail288Url()))
                .thumbnail657Url(generateFullUrl(fileBaseUrl, memePost.getThumbnail657Url()))
                .likeCount(memePost.getLikeCount())
                .viewCount(memePost.getViewCount())
                .downloadCount(memePost.getDownloadCount())
                .isLiked(isLiked)
                .tags(tags)
                .build();
    }


    public static MemePostSummaryResponse from(MemePostSummaryProjection projection, boolean isLiked, List<String> tags, String fileBaseUrl) {
        return MemePostSummaryResponse.builder()
                .id(projection.getId())
                .imageUrl(fileBaseUrl + projection.getImageUrl())
                 .thumbnail288Url(generateFullUrl(fileBaseUrl, projection.getThumbnail288Url()))
                 .thumbnail657Url(generateFullUrl(fileBaseUrl, projection.getThumbnail657Url()))
                .likeCount(projection.getLikeCount())
                .viewCount(projection.getViewCount())
                .downloadCount(projection.getDownloadCount())
                .isLiked(isLiked)
                .tags(tags)
                .processingStatus(projection.getProcessingStatus())
                .build();
    }

    private static String generateFullUrl(String baseUrl, String objectKey) {
        return objectKey != null ? baseUrl + objectKey : null;
    }



}
