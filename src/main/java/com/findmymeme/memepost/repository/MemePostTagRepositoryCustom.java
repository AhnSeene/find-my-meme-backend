package com.findmymeme.memepost.repository;

import com.findmymeme.memepost.dto.MemePostTagProjection;

import java.util.List;

public interface MemePostTagRepositoryCustom {
    List<MemePostTagProjection> findTagNamesByPostId(List<Long> postIds);
}
