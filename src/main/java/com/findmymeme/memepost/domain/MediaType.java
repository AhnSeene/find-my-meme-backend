package com.findmymeme.memepost.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Schema(description = "미디어 타입. STATIC: 정적 이미지, ANIMATED: 움직이는 이미지(GIF)")
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
