package com.findmymeme.tag.dto;

import com.findmymeme.tag.domain.Tag;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class TagSummaryResponse {

    private Long id;
    private String parentTag;
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

    @Getter
    public static class SubTag {
        private Long id;
        private String name;

        public SubTag(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
