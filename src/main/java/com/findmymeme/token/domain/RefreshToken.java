package com.findmymeme.token.domain;

import com.findmymeme.user.domain.Role;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash(value = "refreshToken", timeToLive = 604800)
public class RefreshToken {
    @Id
    private String refresh;
    private Long userId;
    private Role role;
    private LocalDateTime expiredAt;


    @Builder
    public RefreshToken(String refresh, Long userId, Role role, LocalDateTime expiredAt) {
        this.refresh = refresh;
        this.userId = userId;
        this.role = role;
        this.expiredAt = expiredAt;
    }
}
