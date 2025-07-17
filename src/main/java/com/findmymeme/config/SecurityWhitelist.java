package com.findmymeme.config;

public class SecurityWhitelist {
    public static final String[] PUBLIC_AUTH_URLS = {
            "/api/v1/signup",
            "/api/v1/login",
            "/api/v1/reissue",
            "/api/v1/logout",
            "/api/v1/users/check-username",
            "/api/v1/users/check-email",
            "/actuator/**"
    };

    public static final String[] SWAGGER_URLS = {
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**"
    };

    public static final String[] PUBLIC_GET_URLS = {
            "/api/v1/find-posts/**",
            "/api/v1/tags",
            "/api/v1/meme-posts/**"
    };

    public static final String ADMIN_URL = "/api/v1/admin/**";
}
