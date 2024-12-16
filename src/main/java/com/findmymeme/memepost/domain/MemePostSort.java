package com.findmymeme.memepost.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

@Getter
@RequiredArgsConstructor
public enum MemePostSort {
    LIKES("likeCount", Sort.Direction.DESC),
    VIEWS("viewCount", Sort.Direction.DESC),
    CREATED("createdAt", Sort.Direction.DESC);

    private final String field;
    private final Sort.Direction direction;

    public Sort toSort() {
        return Sort.by(direction, field);
    }
}
