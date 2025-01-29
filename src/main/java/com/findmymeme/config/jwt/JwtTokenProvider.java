package com.findmymeme.config.jwt;

import com.findmymeme.user.domain.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static com.findmymeme.config.jwt.TokenStatus.*;

@Slf4j
@Component
public class JwtTokenProvider {

    private static final String CATEGORY = "CATEGORY";
    private static final String USER_ROLE = "ROLE";
    private final JwtProperties jwtProperties;
    private final Key key;

    public JwtTokenProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.key = getSigningKey();
    }

    public String generateToken(Long userId, String role, Long expireTime, TokenCategory category) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tokenExpireTime = now.plusSeconds(expireTime);

        ZoneId zoneId = ZoneId.systemDefault();
        Date issuedAt = Date.from(now.atZone(zoneId).toInstant());
        Date expiration = Date.from(tokenExpireTime.atZone(zoneId).toInstant());
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .claim(CATEGORY, category).setSubject(String.valueOf(userId))
                .claim(USER_ROLE, role)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public TokenStatus validateToken(String token) {
        if (token == null) {
            return INVALID;
        }

        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return VALID;
        } catch (ExpiredJwtException e) {
            return EXPIRED;
        } catch (JwtException | IllegalArgumentException e) {
            return INVALID;
        }
    }

    public Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }


    public Role getRole(String token) {
        String role = parseClaims(token)
                .get(USER_ROLE, String.class);
        return Role.valueOf(role);
    }

    public Long getUserId(String token) {
        String userId = parseClaims(token).getSubject();
        return Long.parseLong(userId);
    }

    public LocalDateTime getExpireTime(String token) {
        Date expiration = parseClaims(token).getExpiration();
        return expiration.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public TokenCategory getTokenCategory(String token) {
        String category = parseClaims(token).get(CATEGORY, String.class);
        return TokenCategory.valueOf(category);
    }


    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecretKey());
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
