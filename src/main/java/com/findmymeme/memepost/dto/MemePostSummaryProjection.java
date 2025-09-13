package com.findmymeme.memepost.dto;

import com.findmymeme.memepost.domain.Extension;
import com.findmymeme.memepost.domain.MediaType;
import com.findmymeme.memepost.domain.ProcessingStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemePostSummaryProjection {

    private Long id;
    private MediaType mediaType;
    private Extension extension;
    private String imageUrl;
    private String thumbnail288Url;
    private String thumbnail657Url;
    private Long likeCount;
    private Long viewCount;
    private Long downloadCount;
    private ProcessingStatus processingStatus;
    private String originalFilename;

    @Builder
    public MemePostSummaryProjection(Long id, String imageUrl, String thumbnail288Url, String thumbnail657Url, Long likeCount,
                                     MediaType mediaType, Extension extension, Long viewCount, Long downloadCount,
                                     ProcessingStatus processingStatus, String originalFilename) {
        this.id = id;
        this.mediaType = mediaType;
        this.extension = extension;
        this.imageUrl = imageUrl;
        this.thumbnail288Url = thumbnail288Url;
        this.thumbnail657Url = thumbnail657Url;
        this.likeCount = likeCount;
        this.viewCount = viewCount;
        this.downloadCount = downloadCount;
        this.processingStatus = processingStatus;
        this.originalFilename = originalFilename;
    }
}
