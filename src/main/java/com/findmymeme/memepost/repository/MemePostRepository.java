package com.findmymeme.memepost.repository;

import com.findmymeme.memepost.domain.MemePost;
import com.findmymeme.memepost.dto.MemePostSummaryResponse;
import com.findmymeme.memepost.dto.MemePostTagProjection;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

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


    @Query("SELECT m.id FROM MemePost m WHERE m.deletedAt IS NULL AND m.processingStatus = 'READY' ORDER BY m.viewCount DESC")
    Slice<Long> findTopPostIdsByViewCount(Pageable pageable);

    @Query("SELECT m.id FROM MemePost m WHERE m.deletedAt IS NULL AND m.processingStatus = 'READY' ORDER BY m.likeCount DESC")
    Slice<Long> findTopPostIdsByLikeCount(Pageable pageable);


    @Query("SELECT mpl.memePost.id " +
            "FROM MemePostLike mpl " +
            "WHERE mpl.createdAt BETWEEN :startDateTime AND :endDateTime " +
            "AND mpl.memePost.deletedAt IS NULL AND mpl.memePost.processingStatus = 'READY' " +
            "GROUP BY mpl.memePost.id " +
            "ORDER BY COUNT(mpl.id) DESC")
    Slice<Long> findTopPostIdsByLikeCountWithinPeriod(@Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT mp FROM MemePost mp WHERE mp.id = :id AND mp.deletedAt IS NULL")
    Optional<MemePost> findWithPessimisticLockById(@Param("id") Long memePostId);

    @Modifying
    @Query("UPDATE MemePost m SET m.likeCount = m.likeCount + 1 WHERE m.id = :postId")
    void incrementLikeCount(@Param("postId") Long postId);

    @Modifying
    @Query("UPDATE MemePost m SET m.likeCount = m.likeCount - 1 WHERE m.id = :postId")
    void decrementLikeCount(@Param("postId") Long postId);

    @Modifying
    @Query("UPDATE MemePost m SET m.viewCount = m.viewCount + 1 WHERE m.id = :postId")
    void incrementViewCount(@Param("postId") Long postId);

    @Modifying
    @Query("UPDATE MemePost m SET m.viewCount = m.viewCount + :increment WHERE m.id = :postId")
    void batchIncrementViewCount(@Param("postId") Long postId, @Param("increment") Long increment);

}
