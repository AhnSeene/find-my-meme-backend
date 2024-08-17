package com.findmymeme.tag.dto;

import com.findmymeme.findpost.domain.FindPostComment;
import com.findmymeme.tag.domain.Tag;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Getter
@NoArgsConstructor
public class TagCreateResponse {

    private String name;
    private String slug;
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
