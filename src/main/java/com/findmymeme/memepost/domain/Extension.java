package com.findmymeme.memepost.domain;

import com.findmymeme.exception.ErrorCode;
import com.findmymeme.exception.FindMyMemeException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum Extension {
    PNG("png"),
    JPG("jpg"),
    JPEG("jpeg"),
    GIF("gif");

    private final String value;

    public static Extension from(String value) {
        return Arrays.stream(values())
                .filter(extension -> extension.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new FindMyMemeException(ErrorCode.REQUEST_INVALID_EXTENSION));
    }
}
