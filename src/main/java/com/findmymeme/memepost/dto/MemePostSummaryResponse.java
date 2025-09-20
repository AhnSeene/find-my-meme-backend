package com.findmymeme.memepost.dto;

import com.findmymeme.memepost.domain.Extension;
import com.findmymeme.memepost.domain.MediaType;
import com.findmymeme.memepost.domain.ProcessingStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "밈 게시물 목록 요약 정보 응답 DTO")
@Getter
@NoArgsConstructor
public class MemePostSummaryResponse {

    @Schema(description = "게시물 ID", example = "1")
    private Long id;

    @Schema(description = "렌더링에 필요한 모든 미디어 정보")
    private MediaInfo mediaInfo;
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
    private MemePostSummaryResponse(Long id, MediaInfo mediaInfo, Long likeCount, Long viewCount, Long downloadCount,
                                    Boolean isLiked, List<String> tags, ProcessingStatus processingStatus) {
        this.id = id;
        this.mediaInfo = mediaInfo;
        this.likeCount = likeCount;
        this.viewCount = viewCount;
        this.downloadCount = downloadCount;
        this.isLiked = isLiked;
        this.tags = tags;
        this.processingStatus = processingStatus;
    }


    public static MemePostSummaryResponse from(MemePostSummaryProjection projection, boolean isLiked, List<String> tags, String fileBaseUrl) {
        return MemePostSummaryResponse.builder()
                .id(projection.getId())
                .mediaInfo(MediaInfo.from(new MemePostSummaryProjectionAdapter(projection), fileBaseUrl))
                .likeCount(projection.getLikeCount())
                .viewCount(projection.getViewCount())
                .downloadCount(projection.getDownloadCount())
                .isLiked(isLiked)
                .tags(tags)
                .processingStatus(projection.getProcessingStatus())
                .build();
    }
    private static class MemePostSummaryProjectionAdapter implements MediaDataProvider {
        private final MemePostSummaryProjection projection;

        public MemePostSummaryProjectionAdapter(MemePostSummaryProjection projection) { this.projection = projection; }

        @Override public String getImageUrl() { return projection.getImageUrl(); }
        @Override public String getThumbnail288Url() { return projection.getThumbnail288Url(); }
        @Override public String getThumbnail657Url() { return projection.getThumbnail657Url(); }
        @Override public MediaType getMediaType() { return projection.getMediaType(); }
        @Override public Extension getExtension() { return projection.getExtension(); }
        @Override public String getOriginalFilename() { return projection.getOriginalFilename(); }
    }

}
