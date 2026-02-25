package com.findmymeme.memepost.repository;

import com.findmymeme.memepost.domain.MemePostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemePostLikeRepository extends JpaRepository<MemePostLike, Long> {

    Optional<MemePostLike> findByMemePostIdAndUserId(Long memePostId, Long userId);

    boolean existsByMemePostIdAndUserId(Long memePostId, Long userId);

    @Query("SELECT mpl.memePostId FROM MemePostLike mpl " +
            "WHERE mpl.memePostId IN :postIds AND mpl.userId = :userId")
    List<Long> findLikedPostIds(@Param("postIds") List<Long> postIds, @Param("userId") Long userId);
}