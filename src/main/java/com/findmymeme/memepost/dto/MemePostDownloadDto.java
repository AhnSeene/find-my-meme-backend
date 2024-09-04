package com.findmymeme.memepost.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.core.io.Resource;

@NoArgsConstructor
@Getter
public class MemePostDownloadDto {
    private String filename;
    private Resource resource;

    @Builder
    public MemePostDownloadDto(String filename, Resource resource) {
        this.filename = filename;
        this.resource = resource;
    }
}