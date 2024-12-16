package com.findmymeme.memepost.repository;

import com.findmymeme.memepost.domain.MemePost;
import com.findmymeme.memepost.dto.MemePostSummaryResponse;
import com.findmymeme.user.domain.User;
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

    @Query("SELECT mp.id FROM MemePost mp where mp.deletedAt IS NULL")
    Slice<Long> findSliceAllIds(Pageable pageable);

    @Query("SELECT mp FROM MemePost mp " +
            "LEFT JOIN FETCH mp.memePostTags mpt " +
            "LEFT JOIN FETCH mpt.tag " +
            "WHERE mp.deletedAt IS NULL order by mp.createdAt desc")
    Slice<MemePost> findAllWithTags(Pageable pageable);

    @Query("SELECT new com.findmymeme.memepost.repository.MemePostTagDto(mp.id, t.name) " +
            "FROM MemePost mp " +
            "JOIN mp.memePostTags mpt " +
            "JOIN mpt.tag t " +
            "WHERE mp IN :memePosts")
    List<MemePostTagDto> findTagNamesByMemePosts(@Param("memePosts") List<MemePost> memePosts);

    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT mp FROM MemePost mp " +
            "WHERE mp.id = :id AND mp.deletedAt IS NULL")
    Optional<MemePost> findWithUserById(@Param("id") Long id);

    @Query("SELECT mp.id FROM MemePost mp " +
            "JOIN MemePostLike mpl ON mpl.memePost = mp " +
            "WHERE mp.id IN :postIds AND mpl.user = :user")
    List<Long> findLikedPostIds(@Param("postIds") List<Long> postIds, @Param("user") User user);
    
    @Query("SELECT new com.findmymeme.memepost.dto.MemePostSummaryResponse(mp, " +
            "EXISTS (SELECT 1 FROM MemePostLike mpl WHERE mpl.memePost = mp AND mpl.user.id = :userId)) " +
            "FROM MemePost mp WHERE mp.deletedAt IS NULL")
    Slice<MemePostSummaryResponse> findMemePostSummariesWithLike(Pageable pageable, @Param("userId") Long userId);

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

    @Query("select distinct m from MemePost m " +
            "left join fetch m.memePostTags mpt " +
            "left join fetch mpt.tag t " +
            "where m.id = :id and m.deletedAt is null")
    Optional<MemePost> findByIdWithTags(@Param("id") Long id);
    @Query("SELECT mp FROM MemePost mp " +
            "LEFT JOIN FETCH mp.memePostTags mpt " +
            "LEFT JOIN FETCH mpt.tag " +
            "WHERE mp.id In :postIds")
    List<MemePost> findAllWithTagsInPostIds(@Param("postIds") List<Long> postIds);

    @Query("select distinct new com.findmymeme.memepost.dto.MemePostSummaryResponse(m, " +
            "exists (select 1 from MemePostLike mpl where mpl.memePost = m and mpl.user.id = :currentUserId)) " +
            "from MemePost m join MemePostTag pt on m.id = pt.memePost.id where pt.tag.name in :tags and m.deletedAt is null")
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
