package com.findmymeme.memepost.repository;

import com.findmymeme.memepost.dto.MemePostSearchCond;
import com.findmymeme.memepost.dto.MemePostSummaryResponse;
import com.findmymeme.memepost.dto.MemePostSummaryProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface MemePostRepositoryCustom {
    List<MemePostSummaryResponse> findPostsWithTags(List<Long> postIds);
    Slice<Long> searchByCond(Pageable pageable, MemePostSearchCond cond);
    List<MemePostSummaryProjection> findPostDetailsByPostIds(List<Long> postIds);
    List<Long> findRelatedPostIdsByTagIds(List<Long> tagIds, Long currentPostId, Pageable pageable);
}
