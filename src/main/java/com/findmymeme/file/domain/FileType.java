package com.findmymeme.file.domain;

import lombok.Getter;

@Getter
public enum FileType {
    MEME("memes"),

    FINDPOST("findposts"),

    COMMENT("comments/findposts"),
    PROFILE("profile");

    private final String prefix;

    FileType(String prefix) {
        this.prefix = prefix;
    }
}
