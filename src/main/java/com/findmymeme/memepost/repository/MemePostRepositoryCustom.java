package com.findmymeme.memepost.repository;

import com.findmymeme.memepost.dto.MemePostSearchCond;
import com.findmymeme.memepost.dto.MemePostSummaryResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface MemePostRepositoryCustom {

    Slice<MemePostSummaryResponse> searchByCond(Pageable pageable, MemePostSearchCond cond);
    Slice<MemePostSummaryResponse> searchByCondWithMemePostLike(Pageable pageable, MemePostSearchCond cond, Long userId);
}
