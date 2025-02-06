package com.findmymeme.config.jwt;

import lombok.Getter;

@Getter
public enum TokenCategory {
    ACCESS, REFRESH;

    public boolean isAccessToken() {
        return this == ACCESS;
    }

    public boolean isRefreshToken() {
        return this == REFRESH;
    }
}
