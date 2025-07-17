package com.findmymeme.memepost.dto;

import com.findmymeme.memepost.domain.MemePost;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "밈 게시물 상세 조회 응답 DTO")
@Getter
@NoArgsConstructor
public class MemePostGetResponse {

    @Schema(description = "게시물 ID", example = "1")
    private Long id;
    @Schema(description = "이미지의 전체 URL", example = "http://localhost:8080/images/memes/2023/10/asdf-qwer-1234.jpg")
    private String imageUrl;
    @Schema(description = "파일 확장자", example = "jpg")
    private String extension;
    @Schema(description = "이미지 높이 (px)", example = "800")
    private int height;
    @Schema(description = "이미지 너비 (px)", example = "600")
    private int weight;
    @Schema(description = "파일 크기 (bytes)", example = "123456")
    private Long size;
    @Schema(description = "원본 파일 이름", example = "my_favorite_meme.jpg")
    private String originalFilename;
    @Schema(description = "좋아요 수", example = "123")
    private Long likeCount;
    @Schema(description = "조회 수", example = "1024")
    private Long viewCount;
    @Schema(description = "다운로드 수", example = "45")
    private Long downloadCount;
    @Schema(description = "작성자 닉네임", example = "meme_master")
    private String username;
    @Schema(description = "작성자 프로필 이미지 URL", example = "http://localhost:8080/images/profile/default.jpg")
    private String userProfileImageUrl;
    @Schema(description = "요청한 사용자가 게시물의 소유주인지 여부", example = "false")
    private boolean owner;
    @Schema(description = "요청한 사용자가 이 게시물에 좋아요를 눌렀는지 여부", example = "true")
    private Boolean isLiked;
    @Schema(description = "생성 일시", example = "2023-10-27T10:00:00")
    private LocalDateTime createdAt;
    @Schema(description = "최종 수정 일시", example = "2023-10-27T11:30:00")
    private LocalDateTime updatedAt;
    @Schema(description = "게시물에 달린 태그 목록", example = "[\"유머\", \"동물\", \"강아지\"]")
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
