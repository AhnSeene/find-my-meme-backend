package com.findmymeme.memepost.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "밈 게시물 업로드 요청 DTO")
@Getter
@NoArgsConstructor
public class MemePostUploadRequest {

    @Schema(description = "S3에 업로드 완료 후 받은 임시 파일 URL. '/files/upload-complete' API를 통해 받은 URL을 사용합니다.", example = "temps/1/asdf-qwer-1234.jpg", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String imageUrl;
    @Schema(description = "게시물에 적용할 태그 ID 목록", example = "[1, 5, 12]", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "{tags.notblank}")
    private List<Long> tags;

    @Builder
    public MemePostUploadRequest(String imageUrl, List<Long> tags) {
        this.imageUrl = imageUrl;
        this.tags = tags;
    }
}
