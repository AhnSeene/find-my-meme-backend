package com.findmymeme.memepost.repository;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemePostTagDto {
    private Long postId;
    private String tagName;

}
