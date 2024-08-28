package com.findmymeme.common.dto;

import com.findmymeme.tag.domain.Tag;
import lombok.Getter;

@Getter
public class TagResponse {
    private Long id;
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
