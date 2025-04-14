package com.findmymeme.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.findmymeme.config.SecurityWhitelist;
import com.findmymeme.exception.ErrorCode;
import com.findmymeme.user.domain.CustomUserDetails;
import com.findmymeme.user.domain.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;
    private final AntPathMatcher antPathMatcher;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return Arrays.stream(SecurityWhitelist.PUBLIC_AUTH_URLS)
                .anyMatch(pattern -> antPathMatcher.match(pattern, request.getServletPath()));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String jwt = resolveToken(request);

        TokenStatus tokenStatus = jwtTokenProvider.validateToken(jwt);

        if (tokenStatus.isInvalid() || !jwtTokenProvider.getTokenCategory(jwt).isAccessToken()) {
            chain.doFilter(request, response);
            return;
        }

        if (tokenStatus.isExpired()) {
            handleExpiredToken(response);
            return;
        }

        authenticateUser(request, response, chain, jwt);
    }

    private void authenticateUser(HttpServletRequest request, HttpServletResponse response, FilterChain chain, String jwt) throws IOException, ServletException {
        Long userId = jwtTokenProvider.getUserId(jwt);
        Role role = jwtTokenProvider.getRole(jwt);
        UserDetails userDetails = new CustomUserDetails(userId, null, role, null);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    private void handleExpiredToken(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", ErrorCode.AUTH_EXPIRED_ACCESS_TOKEN);
        errorResponse.put("error", "Token Error");
        errorResponse.put("message", ErrorCode.AUTH_EXPIRED_ACCESS_TOKEN.getMessage());

        String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(jsonResponse);
    }
}
