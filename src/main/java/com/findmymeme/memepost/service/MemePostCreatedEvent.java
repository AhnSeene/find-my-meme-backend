package com.findmymeme.memepost.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemePostCreatedEvent {
    private final Long memePostId;
    private final String s3ObjectKey;
}
