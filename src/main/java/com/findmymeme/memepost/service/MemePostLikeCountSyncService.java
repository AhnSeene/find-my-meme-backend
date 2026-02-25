package com.findmymeme.memepost.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemePostLikeCountSyncService {

    private final StringRedisTemplate redisTemplate;
    private final MemePostLikeCountWriter likeCountWriter;

    private static final String LIKE_COUNT_HASH_KEY = "meme:like:delta";
    private static final String LIKE_COUNT_PROCESSING_KEY = "meme:like:delta:processing";

    private static final DefaultRedisScript<List> ATOMIC_SWAP_AND_READ_SCRIPT = new DefaultRedisScript<>(
            "local processingExists = redis.call('EXISTS', KEYS[2])\n" +
            "if processingExists == 1 then\n" +
            "    return redis.call('HGETALL', KEYS[2])\n" +
            "end\n" +
            "local deltaExists = redis.call('EXISTS', KEYS[1])\n" +
            "if deltaExists == 0 then\n" +
            "    return {}\n" +
            "end\n" +
            "redis.call('RENAME', KEYS[1], KEYS[2])\n" +
            "return redis.call('HGETALL', KEYS[2])",
            List.class
    );

    @Scheduled(fixedDelay = 60000)
    public void syncLikeCountsToDatabase() {
        Map<Long, Long> postLikeCounts = atomicSwapAndReadLikeCount();

        if (postLikeCounts.isEmpty()) {
            return;
        }

        likeCountWriter.batchUpdateLikeCounts(postLikeCounts);
        redisTemplate.delete(LIKE_COUNT_PROCESSING_KEY);
        log.info("Synced like counts for {} posts to database", postLikeCounts.size());
    }

    private Map<Long, Long> atomicSwapAndReadLikeCount() {
        TreeMap<Long, Long> result = new TreeMap<>();

        List<Object> data = redisTemplate.execute(
                ATOMIC_SWAP_AND_READ_SCRIPT,
                List.of(LIKE_COUNT_HASH_KEY, LIKE_COUNT_PROCESSING_KEY)
        );

        if (data == null || data.isEmpty()) {
            return result;
        }

        for (int i = 0; i < data.size(); i += 2) {
            Long delta = Long.valueOf((String) data.get(i + 1));
            if (delta != 0) {
                result.put(Long.valueOf((String) data.get(i)), delta);
            }
        }

        return result;
    }
}