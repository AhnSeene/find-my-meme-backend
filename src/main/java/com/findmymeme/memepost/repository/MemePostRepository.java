package com.findmymeme.memepost.repository;

import com.findmymeme.memepost.domain.MemePost;
import com.findmymeme.memepost.dto.MemePostSummaryResponse;
import com.findmymeme.memepost.dto.MemePostTagProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MemePostRepository extends JpaRepository<MemePost, Long>, MemePostRepositoryCustom {

    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT mp FROM MemePost mp " +
            "WHERE mp.id = :id AND mp.deletedAt IS NULL")
    Optional<MemePost> findWithUserById(@Param("id") Long id);

    @Query("select m from MemePost m " +
            "join fetch m.memePostTags mpt " +
            "join fetch mpt.tag t " +
            "where m.id = :id and m.deletedAt is null")
    Optional<MemePost> findByIdWithTags(@Param("id") Long id);


    @Query("SELECT m FROM MemePost m WHERE m.deletedAt IS NULL ORDER BY m.viewCount DESC")
    List<MemePost> findTopByViewCount(Pageable pageable);

    @Query("SELECT m FROM MemePost m WHERE m.deletedAt IS NULL ORDER BY m.likeCount DESC")
    List<MemePost> findTopByLikeCount(Pageable pageable);

    @Query("SELECT mpl.memePost " +
            "FROM MemePostLike mpl " +
            "WHERE mpl.createdAt BETWEEN :startOfWeek AND :endOfWeek " +
            "GROUP BY mpl.memePost " +
            "ORDER BY COUNT(mpl.id) DESC")
    List<MemePost> findTopByLikeCountWithinPeriod(@Param("startOfWeek") LocalDateTime startOfWeek, @Param("endOfWeek") LocalDateTime endOfWeek, Pageable pageable);

}
