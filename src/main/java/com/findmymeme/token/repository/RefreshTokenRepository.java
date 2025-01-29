package com.findmymeme.token.repository;

import com.findmymeme.token.domain.RefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
    List<RefreshToken> findAllByUserIdIndex(String userIdIndex);
}
