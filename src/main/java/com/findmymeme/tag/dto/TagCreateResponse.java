package com.findmymeme.tag.dto;

import com.findmymeme.findpost.domain.FindPostComment;
import com.findmymeme.tag.domain.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Schema(description = "새 태그 생성 응답 DTO")
@Getter
@NoArgsConstructor
public class TagCreateResponse {

    @Schema(description = "생성된 태그 이름", example = "강아지")
    private String name;
    @Schema(description = "생성된 태그 슬러그", example = "dog")
    private String slug;
    @Schema(description = "부모 태그의 ID (하위 태그인 경우)", example = "1")
    private Long parentTagId;

    @Builder
    public TagCreateResponse(String name, String slug, Long parentTagId) {
        this.name = name;
        this.slug = slug;
        this.parentTagId = parentTagId;
    }

    public TagCreateResponse(Tag tag) {
        this.name = tag.getName();
        this.slug = tag.getSlug();
        this.parentTagId = Optional.ofNullable(tag.getParentTag())
                .map(Tag::getId)
                .orElse(null);
    }
}
