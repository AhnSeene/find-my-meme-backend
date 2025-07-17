package com.findmymeme.memepost.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.core.io.Resource;

@Schema(description = "밈 게시물 다운로드 응답 DTO (리다이렉션용)")
@NoArgsConstructor
@Getter
public class MemePostDownloadDto {
    @Schema(description = "다운로드될 파일의 이름", example = "findmymeme-my_favorite_meme.jpg")
    private String filename;
    @Schema(description = "파일 다운로드용 Presigned URL. 이 URL로 리다이렉트됩니다.", example = "https://find-my-meme.s3.ap-northeast-2.amazonaws.com/...")
    private String presignedUrl;

    @Builder
    public MemePostDownloadDto(String filename, String presignedUrl) {
        this.filename = filename;
        this.presignedUrl = presignedUrl;
    }
}
