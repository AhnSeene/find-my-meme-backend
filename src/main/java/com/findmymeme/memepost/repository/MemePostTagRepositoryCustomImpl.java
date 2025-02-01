package com.findmymeme.memepost.repository;

import com.findmymeme.memepost.dto.MemePostTagProjection;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.findmymeme.tag.domain.QMemePostTag.memePostTag;

@Repository
@RequiredArgsConstructor
public class MemePostTagRepositoryCustomImpl implements MemePostTagRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    @Override
    public List<MemePostTagProjection> findTagNamesByPostId(List<Long> postIds) {
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
}
