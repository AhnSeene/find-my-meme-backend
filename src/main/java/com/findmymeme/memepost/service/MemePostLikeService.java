package com.findmymeme.memepost.service;

import com.findmymeme.exception.ErrorCode;
import com.findmymeme.exception.FindMyMemeException;
import com.findmymeme.memepost.domain.MemePostLike;
import com.findmymeme.memepost.dto.MemePostLikeResponse;
import com.findmymeme.memepost.repository.MemePostLikeRepository;
import com.findmymeme.memepost.repository.MemePostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemePostLikeService {

    private final MemePostRepository memePostRepository;
    private final MemePostLikeRepository memePostLikeRepository;
    private final StringRedisTemplate redisTemplate;

    private static final String LIKE_COUNT_HASH_KEY = "meme:like:delta";

    public MemePostLikeResponse toggleLike(Long memePostId, Long userId) {
        if (!memePostRepository.existsByIdAndDeletedAtIsNull(memePostId)) {
            throw new FindMyMemeException(ErrorCode.NOT_FOUND_MEME_POST);
        }

        Optional<MemePostLike> existingLike = memePostLikeRepository.findByMemePostIdAndUserId(memePostId, userId);

        boolean isLiked;

        if (existingLike.isPresent()) {
            memePostLikeRepository.delete(existingLike.get());
            redisTemplate.opsForHash().increment(LIKE_COUNT_HASH_KEY, String.valueOf(memePostId), -1);
            isLiked = false;
        } else {
            MemePostLike memePostLike = MemePostLike.builder()
                    .memePostId(memePostId)
                    .userId(userId)
                    .build();
            memePostLikeRepository.save(memePostLike);
            redisTemplate.opsForHash().increment(LIKE_COUNT_HASH_KEY, String.valueOf(memePostId), 1);
            isLiked = true;
        }
        return new MemePostLikeResponse(isLiked);
    }

    public MemePostLikeResponse toggleLikeDb(Long memePostId, Long userId) {
        if (!memePostRepository.existsByIdAndDeletedAtIsNull(memePostId)) {
            throw new FindMyMemeException(ErrorCode.NOT_FOUND_MEME_POST);
        }

        Optional<MemePostLike> existingLike = memePostLikeRepository.findByMemePostIdAndUserId(memePostId, userId);

        boolean isLiked;

        if (existingLike.isPresent()) {
            memePostRepository.decrementLikeCount(memePostId);
            memePostLikeRepository.delete(existingLike.get());
            isLiked = false;
        } else {
            MemePostLike memePostLike = MemePostLike.builder()
                    .memePostId(memePostId)
                    .userId(userId)
                    .build();
            memePostRepository.incrementLikeCount(memePostId);
            memePostLikeRepository.save(memePostLike);
            isLiked = true;
        }
        return new MemePostLikeResponse(isLiked);
    }
}