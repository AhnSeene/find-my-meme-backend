package com.findmymeme.memepost.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Resolution {

    @Column(nullable = false)
    private int width;

    @Column(nullable = false)
    private int height;

    public Resolution(final int width, final int height) {
        this.width = width;
        this.height = height;
    }
}
