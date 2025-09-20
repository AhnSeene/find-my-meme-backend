package com.findmymeme.memepost.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MediaSource {
    private String url;
    private String mimeType;
    private int minWidth;
}
