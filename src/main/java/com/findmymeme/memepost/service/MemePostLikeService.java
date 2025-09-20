package com.findmymeme.memepost.service;

import com.findmymeme.exception.ErrorCode;
import com.findmymeme.exception.FindMyMemeException;
import com.findmymeme.memepost.domain.MemePost;
import com.findmymeme.memepost.domain.MemePostLike;
import com.findmymeme.memepost.dto.MemePostLikeResponse;
import com.findmymeme.memepost.repository.MemePostLikeRepository;
import com.findmymeme.memepost.repository.MemePostRepository;
import com.findmymeme.user.domain.User;
import com.findmymeme.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemePostLikeService {

    private final UserRepository userRepository;
    private final MemePostRepository memePostRepository;
    private final MemePostLikeRepository memePostLikeRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private static final String LIKE_COUNT_KEY = "memepost:like:%d";

    public MemePostLikeResponse toggleLike(Long memePostId, Long userId) {
        User user = getUserById(userId);

        MemePost memePost = getMemePostById(memePostId);

        Optional<MemePostLike> existingLike = memePostLikeRepository.findByMemePostAndUser(memePost, user);

        boolean isLiked = false;
        if (existingLike.isPresent()) {
            memePostRepository.decrementLikeCount(memePostId);
            memePostLikeRepository.delete(existingLike.get());
        } else {
            MemePostLike memePostLike = MemePostLike.builder()
                    .memePost(memePost)
                    .user(user)
                    .build();
            memePostRepository.incrementLikeCount(memePostId);
            memePostLikeRepository.save(memePostLike);
            isLiked = true;
        }
        return new MemePostLikeResponse(isLiked);
    }

    public MemePostLikeResponse toggleLikeRedis(Long memePostId, Long userId) {
        String key = String.format(LIKE_COUNT_KEY, memePostId);
        User user = getUserById(userId);

        MemePost memePost = getMemePostById(memePostId);

        Optional<MemePostLike> existingLike = memePostLikeRepository.findByMemePostAndUser(memePost, user);

        boolean isLiked = false;
        if (existingLike.isPresent()) {
            memePostLikeRepository.delete(existingLike.get());
            redisTemplate.opsForValue().decrement(key);
        } else {
            MemePostLike memePostLike = MemePostLike.builder()
                    .memePost(memePost)
                    .user(user)
                    .build();
            memePostLikeRepository.save(memePostLike);
            redisTemplate.opsForValue().increment(key);
            isLiked = true;
        }
        return new MemePostLikeResponse(isLiked);
    }


    public void incrementLikeBatch(Long memePostId, Long increment) {
        redisTemplate.opsForValue().increment("post:" + memePostId + ":likeCount" , increment);
    }
    
    private MemePost  getMemePostById(Long memePostId) {
        return memePostRepository.findWithUserById(memePostId)
                .orElseThrow(() -> new FindMyMemeException(ErrorCode.NOT_FOUND_MEME_POST));
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new FindMyMemeException(ErrorCode.NOT_FOUND_USER));
    }
}
