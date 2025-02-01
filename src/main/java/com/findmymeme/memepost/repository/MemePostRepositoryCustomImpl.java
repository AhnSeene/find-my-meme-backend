package com.findmymeme.memepost.repository;

import com.findmymeme.common.util.QuerydslSortUtil;
import com.findmymeme.memepost.domain.MediaType;
import com.findmymeme.memepost.dto.MemePostProjection;
import com.findmymeme.memepost.dto.MemePostSearchCond;
import com.findmymeme.memepost.dto.MemePostSummaryResponse;
import com.findmymeme.memepost.dto.TagInfo;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.findmymeme.memepost.domain.QMemePost.memePost;
import static com.findmymeme.memepost.domain.QMemePostLike.memePostLike;
import static com.findmymeme.tag.domain.QMemePostTag.memePostTag;
import static java.util.stream.Collectors.groupingBy;

@Repository
@RequiredArgsConstructor
public class MemePostRepositoryCustomImpl implements MemePostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Long> searchByCond(Pageable pageable, MemePostSearchCond cond) {

        List<Long> postIds = queryFactory
                .select(memePost.id)
                .from(memePost)
                .where(
                        deletedAtIsNull(),
                        mediaTypeEq(cond.getMediaType()),
                        haveTagIds(cond.getTagIds())
                )
                .orderBy(createOrderSpecifiers(pageable))
                .limit(pageable.getPageSize() + 1)
                .offset(pageable.getOffset())
                .fetch();

        boolean hasNext = postIds.size() > pageable.getPageSize();
        if (hasNext) {
            postIds.remove(postIds.size() - 1);
        }

        return new SliceImpl<>(postIds, pageable, hasNext);
    }


    private BooleanExpression haveTagIds(List<Long> tagIds) {
        if (CollectionUtils.isEmpty(tagIds)) {
            return null;
        }

        JPQLQuery<Long> subQuery = queryFactory
                .select(memePostTag.memePost.id)
                .from(memePostTag)
                .where(tagIn(tagIds))
                .groupBy(memePostTag.memePost.id)
                .having(tagCountEq(tagIds));

        return memePost.id.in(subQuery);
    }

    @Override
    public List<MemePostSummaryProjection> findPostDetailsByPostIds(List<Long> postIds) {
        return queryFactory
                .select(Projections.constructor(
                        MemePostSummaryProjection.class,
                        memePost.id,
                        memePost.imageUrl,
                        memePost.likeCount,
                        memePost.viewCount,
                        memePost.downloadCount
                ))
                .from(memePost)
                .where(memePost.id.in(postIds))
                .fetch();
    }


    }


    private BooleanExpression deletedAtIsNull() {
        return memePost.deletedAt.isNull();
    }

    private BooleanExpression mediaTypeEq(MediaType mediaType) {
        if (mediaType == null) {
            return null;
        }
        return memePost.mediaType.eq(mediaType);
    }

    private BooleanExpression tagIn(List<Long> tagIds) {
        if (CollectionUtils.isEmpty(tagIds)) {
            return null;
        }
        return memePostTag.tag.id.in(tagIds);
    }

    private BooleanExpression tagCountEq(List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return null;
        }
        return memePostTag.tag.id.countDistinct().eq((long) tagIds.size());
    }

    private OrderSpecifier<?>[] createOrderSpecifiers(Pageable pageable) {
        return QuerydslSortUtil.getOrderSpecifiers(
                pageable.getSort(),
                new PathBuilder<>(memePost.getType(), memePost.getMetadata())
        );
    }

}
