package com.findmymeme.memepost.repository;

import com.findmymeme.memepost.dto.MemePostTagProjection;

import java.util.List;
import java.util.Optional;

public interface MemePostTagRepositoryCustom {
    List<MemePostTagProjection> findTagNamesInPostIds(List<Long> postIds);
    List<Long> findTagIdsByPostId(Long postId);
}
