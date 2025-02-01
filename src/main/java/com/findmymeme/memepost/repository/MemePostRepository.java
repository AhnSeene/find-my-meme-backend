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

    @Query("SELECT mp FROM MemePost mp " +
            "LEFT JOIN FETCH mp.memePostTags mpt " +
            "LEFT JOIN FETCH mpt.tag " +
            "WHERE mp.deletedAt IS NULL AND mp.user.username = :authorName")
    Slice<MemePost> findMemePostByUsername(Pageable pageable, @Param("authorName") String authorName);

    @Query("SELECT mp.id FROM MemePost mp " +
            "WHERE mp.deletedAt IS NULL AND mp.user.username = :authorName")
    Slice<Long> findSliceByUsername(Pageable pageable, @Param("authorName") String authorName);

    @Query("SELECT new com.findmymeme.memepost.dto.MemePostSummaryResponse(mp, " +
            "EXISTS (SELECT 1 FROM MemePostLike mpl WHERE mpl.memePost = mp AND mpl.user.id = :currentUserId)) " +
            "FROM MemePost mp WHERE mp.deletedAt IS NULL AND mp.user.username = :authorName")
    Slice<MemePostSummaryResponse> findMemePostSummariesWithLikeByAuthorNameAndUserId(
            Pageable pageable,
            @Param("authorName") String authorName,
            @Param("currentUserId") Long currentUserId);

    @Query("select m from MemePost m " +
            "join m.memePostTags mpt " +
            "join mpt.tag t " +
            "where t.name in :tags " +
            "and m.id != :currentPostId " +
            "and m.deletedAt is null")
    List<MemePost> findRelatedPostsByTagNames(@Param("tags") List<String> tags, @Param("currentPostId") Long currentPostId, Pageable pageable);

    @Query("select distinct m.id from MemePost m " +
            "join m.memePostTags mpt " +
            "join mpt.tag t " +
            "where t.name in :tags " +
            "and m.id != :currentPostId " +
            "and m.deletedAt is null")
    List<Long> findRelatedPostIdsByTagNames(@Param("tags") List<String> tags, @Param("currentPostId") Long currentPostId, Pageable pageable);

    @Query("select m from MemePost m " +
            "left join fetch m.memePostTags mpt " +
            "left join fetch mpt.tag t " +
            "where m.id = :id and m.deletedAt is null")
    Optional<MemePost> findByIdWithTags(@Param("id") Long id);

    @Query("SELECT mp FROM MemePost mp " +
            "LEFT JOIN FETCH mp.memePostTags mpt " +
            "LEFT JOIN FETCH mpt.tag " +
            "WHERE mp.id In :postIds")
    List<MemePost> findAllWithTagsInPostIds(@Param("postIds") List<Long> postIds);

    @Query("select new com.findmymeme.memepost.dto.MemePostSummaryResponse(m, " +
            "exists (select 1 from MemePostLike mpl where mpl.memePost = m and mpl.user.id = :currentUserId)) " +
            "from MemePost m " +
            "join MemePostTag mpt on m.id = mpt.memePost.id " +
            "where mpt.tag.name in :tags and m.deletedAt is null")
    List<MemePostSummaryResponse> findByTagNamesWithLikeByUserId(@Param("tags") List<String> tags, Pageable pageable, @Param("currentUserId") Long currentUserId);

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
