package com.findmymeme.memepost.repository;

import com.findmymeme.memepost.domain.MemePost;
import com.findmymeme.memepost.domain.MemePostLike;
import com.findmymeme.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemePostLikeRepository extends JpaRepository<MemePostLike, Long> {

    Optional<MemePostLike> findByMemePostAndUser(MemePost memePost, User user);
    boolean existsByMemePostIdAndUserId(Long memePostId, Long userId);
    @Query("SELECT mpl.memePost.id FROM MemePostLike mpl "  +
            "WHERE mpl.memePost.id IN :postIds AND mpl.user.id = :userId")
    List<Long> findLikedPostIds(@Param("postIds") List<Long> postIds, @Param("userId") Long userId);

}
