package com.findmymeme.memepost.repository;

import com.findmymeme.memepost.domain.MemePost;
import com.findmymeme.memepost.dto.MemePostSummaryResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemePostRepository extends JpaRepository<MemePost, Long> {
    @Query("SELECT mp FROM MemePost mp where mp.deletedAt IS NULL")
    Slice<MemePost> findSliceAll(Pageable pageable);

    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT mp FROM MemePost mp WHERE mp.id = :id AND mp.deletedAt IS NULL")
    Optional<MemePost> findWithUserById(@Param("id") Long id);
    
    @Query("SELECT new com.findmymeme.memepost.dto.MemePostSummaryResponse(mp, " +
            "EXISTS (SELECT 1 FROM MemePostLike mpl WHERE mpl.memePost = mp AND mpl.user.id = :userId)) " +
            "FROM MemePost mp WHERE mp.deletedAt IS NULL")
    Slice<MemePostSummaryResponse> findMemePostSummariesWithLike(Pageable pageable, @Param("userId") Long userId);

    @Query("SELECT new com.findmymeme.memepost.dto.MemePostSummaryResponse(mp, " +
            "EXISTS (SELECT 1 FROM MemePostLike mpl WHERE mpl.memePost = mp AND mpl.user.id = :currentUserId)) " +
            "FROM MemePost mp WHERE mp.deletedAt IS NULL AND mp.user.id = :targetUserId")
    Slice<MemePostSummaryResponse> findMemePostSummariesWithLikeByUserId(
            Pageable pageable,
            @Param("targetUserId") Long targetUserId,
            @Param("currentUserId") Long currentUserId);


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
