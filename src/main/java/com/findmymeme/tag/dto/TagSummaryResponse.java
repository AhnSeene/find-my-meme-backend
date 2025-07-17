package com.findmymeme.tag.dto;

import com.findmymeme.tag.domain.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "태그 목록 요약 정보 응답 DTO (계층 구조)")
@Getter
@NoArgsConstructor
public class TagSummaryResponse {

    @Schema(description = "부모 태그의 ID", example = "1")
    private Long id;
    @Schema(description = "부모 태그의 이름", example = "동물")
    private String parentTag;
    @Schema(description = "부모 태그에 속한 하위 태그 목록")
    private List<SubTag> subTags;

    public static TagSummaryResponse fromEntity(Tag tag) {
        return TagSummaryResponse.builder()
                .id(tag.getId())
                .parentTag(tag.getName())
                .subTags(tag.getSubTags().stream()
                        .map(subTag -> new SubTag(subTag.getId(), subTag.getName()))
                        .toList()
                ).build();
    }

    @Builder
    public TagSummaryResponse(Long id, String parentTag, List<SubTag> subTags) {
        this.id = id;
        this.parentTag = parentTag;
        this.subTags = subTags;
    }

    @Schema(description = "하위 태그 정보")
    @Getter
    public static class SubTag {
        @Schema(description = "하위 태그의 ID", example = "5")
        private Long id;
        @Schema(description = "하위 태그의 이름", example = "강아지")
        private String name;

        public SubTag(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
