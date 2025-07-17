package com.findmymeme.memepost.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "밈 게시물 업로드 응답 DTO")
@Getter
@NoArgsConstructor
public class MemePostUploadResponse {

    @Schema(description = "서버에 영구 저장된 이미지의 경로 (S3 Object Key)", example = "images/memes/2023/10/asdf-qwer-1234.jpg")
    private String imageUrl;
    @Schema(description = "게시물에 적용된 태그 이름 목록", example = "[\"유머\", \"동물\"]")
    private List<String> tags;

    @Builder
    public MemePostUploadResponse(String imageUrl, List<String> tags) {
        this.imageUrl = imageUrl;
        this.tags = tags;
    }
}
