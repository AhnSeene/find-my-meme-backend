package com.findmymeme.memepost.repository;

import com.findmymeme.memepost.dto.MemePostSearchCond;
import com.findmymeme.memepost.dto.MemePostSummaryResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface MemePostRepositoryCustom {
import java.util.List;

public interface MemePostRepositoryCustom {
    Slice<Long> searchByCond(Pageable pageable, MemePostSearchCond cond);
}
