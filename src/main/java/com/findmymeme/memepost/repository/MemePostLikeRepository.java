package com.findmymeme.memepost.repository;

import com.findmymeme.memepost.domain.MemePost;
import com.findmymeme.memepost.domain.MemePostLike;
import com.findmymeme.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemePostLikeRepository extends JpaRepository<MemePostLike, Long> {
    Optional<MemePostLike> findByMemePostAndUser(MemePost memePost, User user);
    boolean existsByMemePostIdAndUserId(Long memePostId, Long userId);
}
