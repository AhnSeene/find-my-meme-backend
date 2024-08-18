package com.findmymeme.memepost.repository;

import com.findmymeme.memepost.domain.MemePost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MemePostRepository extends JpaRepository<MemePost, Long> {
    @Query("SELECT mp FROM MemePost mp")
    Slice<MemePost> findSliceAll(Pageable pageable);


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
