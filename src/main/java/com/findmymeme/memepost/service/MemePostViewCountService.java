package com.findmymeme.memepost.service;

import com.findmymeme.memepost.repository.MemePostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


@Service
@RequiredArgsConstructor
@Transactional
public class MemePostViewCountService {
    private final MemePostRepository memePostRepository;
    private final StringRedisTemplate redisTemplate;
    private static final String VIEW_COUNT_PREFIX = "meme:view:";
    public void incrementViewCount(Long postId) {
        memePostRepository.incrementViewCount(postId);
    }

    public void incrementViewCountRedis(Long postId) {
        String key = VIEW_COUNT_PREFIX + postId;
        redisTemplate.opsForValue().increment(key, 1L);
    }


    @Scheduled(fixedRate = 60000)
    @Transactional
    public void syncViewCountsToDatabase() {
        Set<String> keys = redisTemplate.keys(VIEW_COUNT_PREFIX + "*");
        if (keys == null || keys.isEmpty()) {
            return;
        }

        Map<Long, Long> postViewCounts = new HashMap<>();

        for (String key : keys) {
            String postIdStr = key.substring(VIEW_COUNT_PREFIX.length());
            Long postId = Long.valueOf(postIdStr);
            String countStr = redisTemplate.opsForValue().get(key);

            if (countStr != null) {
                Long count = Long.valueOf(countStr);
                postViewCounts.put(postId, count);

                redisTemplate.delete(key);
            }
        }

        for (Map.Entry<Long, Long> entry : postViewCounts.entrySet()) {
            memePostRepository.batchIncrementViewCount(entry.getKey(), entry.getValue());
        }
    }
}
