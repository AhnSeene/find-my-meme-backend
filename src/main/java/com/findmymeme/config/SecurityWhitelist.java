package com.findmymeme.config;

public class SecurityWhitelist {
    public static final String[] PUBLIC_AUTH_URLS = {
            "/api/v1/signup",
            "/api/v1/login",
            "/api/v1/reissue",
            "/api/v1/logout",
            "/api/v1/users/check-username",
            "/actuator/**"
    };

    public static final String[] PUBLIC_GET_URLS = {
            "/api/v1/find-posts/**",
            "/api/v1/tags",
            "/api/v1/meme-posts/**"
    };

    public static final String ADMIN_URL = "/api/v1/admin/**";
}
