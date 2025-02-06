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

    private MemePost  getMemePostById(Long memePostId) {
        return memePostRepository.findWithUserById(memePostId)
                .orElseThrow(() -> new FindMyMemeException(ErrorCode.NOT_FOUND_MEME_POST));
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new FindMyMemeException(ErrorCode.NOT_FOUND_USER));
    }
}
