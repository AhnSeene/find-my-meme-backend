package com.findmymeme.memepost.dto;

import com.findmymeme.memepost.domain.MediaType;
import lombok.Getter;

import java.util.List;

@Getter
public class MemePostSearchCond {

    private MediaType mediaType;
    private List<Long> tagIds;

    public MemePostSearchCond(MediaType mediaType, List<Long> tagIds) {
        this.mediaType = mediaType;
        this.tagIds = tagIds;
    }
}
