package com.findmymeme.common.dto;

import com.findmymeme.tag.domain.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "태그 정보 응답 DTO")
@Getter
public class TagResponse {
    @Schema(description = "태그 ID", example = "1")
    private Long id;
    @Schema(description = "태그 이름", example = "유머")
    private String name;

    public TagResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public TagResponse(Tag tag) {
        this.id = tag.getId();
        this.name = tag.getName();
    }
}
