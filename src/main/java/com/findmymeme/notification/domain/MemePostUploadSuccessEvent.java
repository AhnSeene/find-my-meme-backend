package com.findmymeme.notification.domain;

import lombok.Getter;

public record MemePostUploadSuccessEvent(Long userId, Long memePostId) {}
