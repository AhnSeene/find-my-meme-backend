package com.findmymeme.memepost.repository;

import com.findmymeme.memepost.dto.MemePostTagProjection;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

import static com.findmymeme.tag.domain.QMemePostTag.memePostTag;

@Repository
@RequiredArgsConstructor
public class MemePostTagRepositoryCustomImpl implements MemePostTagRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    @Override
    public List<MemePostTagProjection> findTagNamesInPostIds(List<Long> postIds) {
        if (CollectionUtils.isEmpty(postIds)) {
            return Collections.emptyList();
        }

        return queryFactory
                .select(Projections.constructor(
                        MemePostTagProjection.class,
                        memePostTag.memePost.id,
                        memePostTag.tag.name
                ))
                .from(memePostTag)
                .where(memePostTag.memePost.id.in(postIds))
                .fetch();
    }

    public List<Long> findTagIdsByPostId(Long postId) {
        return queryFactory
                .select(memePostTag.tag.id)
                .from(memePostTag)
                .where(memePostTag.memePost.id.eq(postId))
                .fetch();
    }

}
