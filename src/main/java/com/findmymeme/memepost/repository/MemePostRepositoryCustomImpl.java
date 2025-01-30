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


//    @Override
//    public Slice<MemePostSummaryResponse> searchByCond(Pageable pageable, MemePostSearchCond cond) {
//        List<MemePostSummaryResponse> responses = queryFactory
//                .select(Projections.constructor(
//                        MemePostSummaryResponse.class,
//                        memePost.id,
//                        memePost.imageUrl,
//                        memePost.likeCount,
//                        memePost.viewCount,
//                        memePost.downloadCount
//                ))
//                .from(memePost)
//                .join(memePost.memePostTags, memePostTag)
//                .where(deletedAtIsNull(), mediaTypeEq(cond.getMediaType()), tagIn(cond.getTagIds()))
//                .groupBy(memePost.id)
//                .having(tagCountEq(cond.getTagIds()))
//                .orderBy(createOrderSpecifiers(pageable))
//                .limit(pageable.getPageSize() + 1)
//                .offset(pageable.getOffset())
//                .fetch();
//
//        boolean hasNext = responses.size() > pageable.getPageSize();
//        if (hasNext) {
//            responses.remove(responses.size() - 1);
//        }
//
//        return new SliceImpl<>(responses, pageable, hasNext);
//    }

    @Override
    public Slice<Long> searchByCond(Pageable pageable, MemePostSearchCond cond) {
        JPQLQuery<Long> query = queryFactory
                .select(memePost.id)
                .from(memePost)
                .join(memePost.memePostTags, memePostTag)
                .where(deletedAtIsNull(), mediaTypeEq(cond.getMediaType()), tagIn(cond.getTagIds()));


        addGroupByAndHavingTagCount(query, cond.getTagIds());

        List<Long> postIds = query
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
        if (tagIds == null || tagIds.isEmpty()) {
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

    private void addGroupByAndHavingTagCount(JPQLQuery<?> query, List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return;
        }
        query.groupBy(memePost.id)
                .having(tagCountEq(tagIds));
    }

    private OrderSpecifier<?>[] createOrderSpecifiers(Pageable pageable) {
        return QuerydslSortUtil.getOrderSpecifiers(
                pageable.getSort(),
                new PathBuilder<>(memePost.getType(), memePost.getMetadata())
        );
    }

}
