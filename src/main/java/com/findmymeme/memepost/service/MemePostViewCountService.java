package com.findmymeme.memepost.service;

import com.findmymeme.memepost.repository.MemePostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class MemePostViewCountService {
    private final MemePostRepository memePostRepository;

    public void incrementViewCount(Long postId) {
        memePostRepository.incrementViewCount(postId);
    }
}
