package com.findmymeme.memepost.repository;

import com.findmymeme.memepost.domain.MemePost;
import com.findmymeme.memepost.dto.MemePostSearchCond;
import com.findmymeme.memepost.dto.MemePostSummaryResponse;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.findmymeme.memepost.domain.QMemePost.memePost;
import static com.findmymeme.memepost.domain.QMemePostLike.memePostLike;
import static com.findmymeme.tag.domain.QMemePostTag.memePostTag;
import static com.findmymeme.tag.domain.QTag.tag;

@Repository
@RequiredArgsConstructor
public class MemePostRepositoryCustomImpl implements MemePostRepositoryCustom {

    private final JPAQueryFactory queryFactory;


    @Override
    public Slice<MemePostSummaryResponse> searchByCond(Pageable pageable, MemePostSearchCond cond) {

        List<MemePostSummaryResponse> responses = queryFactory
                .select(Projections.constructor(
                        MemePostSummaryResponse.class,
                        memePost.id,
                        memePost.imageUrl,
                        memePost.likeCount,
                        memePost.viewCount,
                        memePost.downloadCount
                ))
                .from(memePost)
                .join(memePost.memePostTags, memePostTag)
                .where(usernameLike(cond.getUsername()), tagIn(cond.getTagIds()))
                .groupBy(memePost.id)
                .having(tagCountEq(cond.getTagIds()))
                .limit(pageable.getPageSize() + 1)
                .offset(pageable.getOffset())
                .fetch();

        boolean hasNext = responses.size() > pageable.getPageSize();
        if (hasNext) {
            responses.remove(responses.size() - 1);
        }

        return new SliceImpl<>(responses, pageable, hasNext);
    }

    @Override
    public Slice<MemePostSummaryResponse> searchByCondWithMemePostLike(Pageable pageable, MemePostSearchCond cond, Long userId) {
        List<MemePostSummaryResponse> responses = queryFactory
                .select(Projections.constructor(
                        MemePostSummaryResponse.class,
                        memePost.id,
                        memePost.imageUrl,
                        memePost.likeCount,
                        memePost.viewCount,
                        memePost.downloadCount,
                        memePostLike.isNotNull()
                ))
                .from(memePost)
                .join(memePostTag)
                .leftJoin(memePostLike)
                .on(memePostLike.memePost.id.eq(memePost.id), memePostLike.user.id.eq(userId))
                .where(usernameLike(cond.getUsername()), tagIn(cond.getTagIds()))
                .groupBy(memePost.id)
                .having(tagCountEq(cond.getTagIds()))
                .limit(pageable.getPageSize() + 1)
                .offset(pageable.getOffset())
                .fetch();

        boolean hasNext = responses.size() > pageable.getPageSize();
        if (hasNext) {
            responses.remove(responses.size() - 1);
        }

        return new SliceImpl<>(responses, pageable, hasNext);
    }


    private BooleanExpression usernameLike(String username) {
        if (!StringUtils.hasText(username)) {
            return Expressions.TRUE;
        }
        return memePost.user.username.eq(username);
    }

    private BooleanExpression tagIn(List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return Expressions.TRUE;
        }
        return memePostTag.tag.id.in(tagIds);
    }
    private BooleanExpression tagCountEq(List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return Expressions.TRUE;
        }
        return memePostTag.tag.id.countDistinct().eq((long) tagIds.size());
    }
}
