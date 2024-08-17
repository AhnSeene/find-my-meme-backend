package com.findmymeme.tag.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TagCreateRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String slug;

    private Long parentTagId;

    @Builder
    public TagCreateRequest(String name, String slug) {
        this.name = name;
        this.slug = slug;
    }
}
