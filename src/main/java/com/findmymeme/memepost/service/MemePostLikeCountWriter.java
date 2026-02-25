package com.findmymeme.memepost.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class MemePostLikeCountWriter {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void batchUpdateLikeCounts(Map<Long, Long> postLikeCounts) {
        String sql = "UPDATE meme_post SET like_count = like_count + ? WHERE id = ?";

        List<Map.Entry<Long, Long>> entries = new ArrayList<>(postLikeCounts.entrySet());

        jdbcTemplate.batchUpdate(sql, entries, entries.size(),
                (ps, entry) -> {
                    ps.setLong(1, entry.getValue());
                    ps.setLong(2, entry.getKey());
                });
    }
}