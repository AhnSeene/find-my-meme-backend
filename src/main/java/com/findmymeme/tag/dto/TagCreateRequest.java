package com.findmymeme.tag.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "새 태그 생성 요청 DTO")
@Getter
@NoArgsConstructor
public class TagCreateRequest {

    @Schema(description = "태그 이름", example = "강아지", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String name;

    @Schema(description = "태그 슬러그", example = "dog", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String slug;

    @Schema(description = "부모 태그의 ID (하위 태그로 만들 경우)", example = "1")
    private Long parentTagId;

    @Builder
    public TagCreateRequest(String name, String slug, Long parentTagId) {
        this.name = name;
        this.slug = slug;
        this.parentTagId = parentTagId;
    }
}
