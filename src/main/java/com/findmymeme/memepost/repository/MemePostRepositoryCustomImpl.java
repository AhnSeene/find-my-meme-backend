package com.findmymeme.memepost.repository;

import com.findmymeme.common.util.QuerydslSortUtil;
import com.findmymeme.memepost.domain.MediaType;
import com.findmymeme.memepost.dto.*;
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
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

import static com.findmymeme.memepost.domain.QMemePost.memePost;
import static com.findmymeme.tag.domain.QMemePostTag.memePostTag;
import static com.findmymeme.tag.domain.QTag.tag;
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


    @Override
    public List<MemePostSummaryResponse> findPostsWithTags(List<Long> postIds) {
        return queryFactory
                .select(Projections.fields(
                        MemePostSummaryResponse.class,
                        memePost.id.as("id"),
                        memePost.imageUrl.as("imageUrl"),
                        memePost.likeCount.as("likeCount"),
                        memePost.viewCount.as("viewCount"),
                        memePost.downloadCount.as("downloadCount"),
                        memePostTag.tag.name.as("tags")
                ))
                .from(memePost)
                .innerJoin(memePost.memePostTags, memePostTag)
                .where(memePost.id.in(postIds))
                .fetch();
    }
    @Override
    public List<Long> findRelatedPostIdsByTagIds(List<Long> tagIds, Long currentPostId, Pageable pageable) {
        return queryFactory
                .select(memePost.id).distinct()
                .from(memePost)
                .join(memePost.memePostTags, memePostTag)
                .where(
                        tagIn(tagIds),
                        memePost.id.ne(currentPostId),
                        deletedAtIsNull()
                )
                .limit(pageable.getPageSize() + 1)
                .offset(pageable.getOffset())
                .fetch();
    }

    @Override
    public Slice<Long> findMemePostIdsByUsername(Pageable pageable, String authorName) {
        List<Long> postIds = queryFactory
                .select(memePost.id)
                .from(memePost)
                .innerJoin(memePost.user, user)
                .where(
                        deletedAtIsNull(),
                        usernameEq(authorName)
                )
                .orderBy(memePost.createdAt.desc())
                .limit(pageable.getPageSize() + 1)
                .offset(pageable.getOffset())
                .fetch();

        boolean hasNext = postIds.size() > pageable.getPageSize();
        if (hasNext) {
            postIds.remove(postIds.size() - 1);
        }

        return new SliceImpl<>(postIds, pageable, hasNext);
    }

    private BooleanExpression usernameEq(String username) {
        return memePost.user.username.eq(username);
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
        if (CollectionUtils.isEmpty(tagIds)) {
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
