package com.findmymeme.memepost.repository;

import com.findmymeme.memepost.domain.MemePost;
import com.findmymeme.memepost.dto.MemePostSummaryResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MemePostRepository extends JpaRepository<MemePost, Long>, MemePostRepositoryCustom {
    @Query("SELECT mp FROM MemePost mp where mp.deletedAt IS NULL")
    Slice<MemePost> findSliceAll(Pageable pageable);

    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT mp FROM MemePost mp WHERE mp.id = :id AND mp.deletedAt IS NULL")
    Optional<MemePost> findWithUserById(@Param("id") Long id);
    
    @Query("SELECT new com.findmymeme.memepost.dto.MemePostSummaryResponse(mp, " +
            "EXISTS (SELECT 1 FROM MemePostLike mpl WHERE mpl.memePost = mp AND mpl.user.id = :userId)) " +
            "FROM MemePost mp WHERE mp.deletedAt IS NULL")
    Slice<MemePostSummaryResponse> findMemePostSummariesWithLike(Pageable pageable, @Param("userId") Long userId);

    @Query("SELECT mp FROM MemePost mp WHERE mp.deletedAt IS NULL AND mp.user.username = :authorName")
    Slice<MemePost> findMemePostByUserId(
            Pageable pageable,
            @Param("authorName") String authorName);

    @Query("SELECT new com.findmymeme.memepost.dto.MemePostSummaryResponse(mp, " +
            "EXISTS (SELECT 1 FROM MemePostLike mpl WHERE mpl.memePost = mp AND mpl.user.id = :currentUserId)) " +
            "FROM MemePost mp WHERE mp.deletedAt IS NULL AND mp.user.username = :authorName")
    Slice<MemePostSummaryResponse> findMemePostSummariesWithLikeByUserId(
            Pageable pageable,
            @Param("authorName") String authorName,
            @Param("currentUserId") Long currentUserId);

    @Query("select distinct m from MemePost m join PostTag pt on m.id = pt.postId where pt.tag.name in :tags and m.deletedAt is null")
    List<MemePost> findByTagNames(@Param("tags") List<String> tags, Pageable pageable);

    @Query("select distinct new com.findmymeme.memepost.dto.MemePostSummaryResponse(m, " +
            "exists (select 1 from MemePostLike mpl where mpl.memePost = m and mpl.user.id = :currentUserId)) " +
            "from MemePost m join PostTag pt on m.id = pt.postId where pt.tag.name in :tags and m.deletedAt is null")
    List<MemePostSummaryResponse> findByTagNamesWithLikeByUserId(@Param("tags") List<String> tags, Pageable pageable, @Param("currentUserId") Long currentUserId);

    @Query("SELECT m FROM MemePost m WHERE m.deletedAt IS NULL ORDER BY m.viewCount DESC")
    List<MemePost> findTopByViewCount(Pageable pageable);

    @Query("SELECT m FROM MemePost m WHERE m.deletedAt IS NULL ORDER BY m.likeCount DESC")
    List<MemePost> findTopByLikeCount(Pageable pageable);

    @Query("SELECT mpl.memePost FROM MemePostLike mpl WHERE mpl.createdAt BETWEEN :startOfWeek AND :endOfWeek GROUP BY mpl.memePost ORDER BY COUNT(mpl.id) DESC")
    List<MemePost> findTopByLikeCountWithinPeriod(@Param("startOfWeek") LocalDateTime startOfWeek, @Param("endOfWeek") LocalDateTime endOfWeek, Pageable pageable);

//    @Query("select mp from MemePost mp join PostTag pt on mp.id = pt.postId " +
//            "where pt.tag.id in :tagIds " +
//            "group by mp.id " +
//            "having count(distinct pt.tag.id) = size(:tagIds) ")
//    Slice<MemePostSummaryResponse> findByTagIds(Pageable pageable, @Param("tagIds") List<Long> tagIds);

//    @Query("select new com.findmymeme.memepost.dto.MemePostSummaryResponse(" +
//            "mp.id, mp.imageUrl, mp.likeCount, mp.viewCount, " +
//            "pt.tag.name) " +
//            "from MemePost mp " +
//            "left join PostTag pt on pt.postId = mp.id and pt.postType = 'MEME_POST' " +
//            "group by mp.id")
//    Slice<MemePostSummaryResponse> findAllWithTags(Pageable pageable);
//    @Query("select new com.findmymeme.memepost.dto.MemePostSummaryResponse(" +
//            "mp.id, mp.imageUrl, mp.likeCount, mp.viewCount, " +
//            "string_agg(pt.tag.name, ', ') as tags) " +
//            "from MemePost mp " +
//            "join PostTag pt on pt.postId = mp.id and pt.postType = 'MEME_POST' " +
//            "join pt.tag " +
//            "group by mp.id")
//    Slice<MemePostSummaryResponse> findAllWithTags(Pageable pageable);


//    @Query("select new com.findmymeme.memepost.dto.MemePostSummaryResponse(" +
//            "mp.id, mp.imageUrl, mp.likeCount, mp.viewCount, " +
//            "collect(pt.tag.name)) " +
//            "from MemePost mp " +
//            "join PostTag pt on pt.postId = mp.id and pt.postType = 'MEME_POST' " +
//            "join pt.tag " +
//            "where mp.id = :postId " +
//            "group by mp")
//    Optional<MemePost> findByWithTags(@Param(("postId")) Long postId);
}
