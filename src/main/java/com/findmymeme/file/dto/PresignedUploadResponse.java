package com.findmymeme.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "파일 업로드용 Presigned URL 생성 응답 DTO")
@Getter
@NoArgsConstructor
public class PresignedUploadResponse {
    @Schema(description = "파일 업로드를 위한 S3 Presigned URL. 클라이언트는 이 URL로 파일을 PUT 요청해야 합니다.", example = "https://find-my-meme.s3.ap-northeast-2.amazonaws.com/temps/1/asdf-qwer-1234.jpg?X-Amz-Algorithm=...")
    private String presignedUrl;

    public PresignedUploadResponse(String presignedUrl) {
        this.presignedUrl = presignedUrl;
    }
}
