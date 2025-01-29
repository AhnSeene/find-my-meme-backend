package com.findmymeme.config.jwt;

import lombok.Getter;

@Getter
public enum TokenStatus {
    VALID, INVALID, EXPIRED;

    public boolean isExpired() {
        return this == EXPIRED;
    }

    public boolean isInvalid() {
        return this == INVALID;
    }

    public boolean isUnauthorized() {
        return this == INVALID || this == EXPIRED;
    }
}
