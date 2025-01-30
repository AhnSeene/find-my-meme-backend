package com.findmymeme.memepost.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MediaType {
    STATIC("정적 이미지"),
    ANIMATED("움직이는 이미지");

    private final String description;

    public static MediaType fromExtension(String extension) {
        if (Extension.GIF.getValue().equalsIgnoreCase(extension)) {
            return ANIMATED;
        }
        return STATIC;
    }
}
