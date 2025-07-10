package com.findmymeme.memepost.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ImageResizerPayload {
    private Long memePostId;
    private String s3ObjectKey;
}
