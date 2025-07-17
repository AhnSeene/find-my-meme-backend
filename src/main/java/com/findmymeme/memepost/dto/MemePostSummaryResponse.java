package com.findmymeme.memepost.dto;

import com.findmymeme.memepost.domain.MemePost;
import com.findmymeme.memepost.domain.ProcessingStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "밈 게시물 목록 요약 정보 응답 DTO")
@Getter
@NoArgsConstructor
public class MemePostSummaryResponse {

    @Schema(description = "게시물 ID", example = "1")
    private Long id;
    @Schema(description = "이미지의 전체 URL. 썸네일이 없을 경우 이 URL을 사용합니다.", example = "http://localhost:8080/images/memes/2023/10/asdf-qwer-1234.jpg")
    private String imageUrl;
    @Schema(description = "288px 너비 썸네일 URL", example = "http://localhost:8080/images/memes/2023/10/asdf-qwer-1234_288.webp")
    private String thumbnail288Url;
    @Schema(description = "657px 너비 썸네일 URL", example = "http://localhost:8080/images/memes/2023/10/asdf-qwer-1234_657.webp")
    private String thumbnail657Url;
    @Schema(description = "썸네일 처리 상태 (READY: 완료, PROCESSING: 처리중, FAILED: 실패)", example = "READY")
    private ProcessingStatus processingStatus;
    @Schema(description = "좋아요 수", example = "123")
    private Long likeCount;
    @Schema(description = "조회 수", example = "1024")
    private Long viewCount;
    @Schema(description = "다운로드 수", example = "45")
    private Long downloadCount;
    @Schema(description = "요청한 사용자가 이 게시물에 좋아요를 눌렀는지 여부", example = "false")
    private Boolean isLiked = false;
    @Schema(description = "게시물에 달린 태그 목록", example = "[\"유머\", \"동물\", \"강아지\"]")
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
