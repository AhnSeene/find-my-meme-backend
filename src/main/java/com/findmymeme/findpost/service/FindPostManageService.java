package com.findmymeme.findpost.service;

import com.findmymeme.exception.ErrorCode;
import com.findmymeme.exception.FindMyMemeException;
import com.findmymeme.findpost.domain.FindPost;
import com.findmymeme.findpost.dto.FindPostGetResponse;
import com.findmymeme.findpost.repository.FindPostRepository;
import com.findmymeme.user.User;
import com.findmymeme.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FindPostManageService {

    private final UserRepository userRepository;
    private final FindPostRepository findPostRepository;

    public FindPostGetResponse getFindPost(Long findPostId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new FindMyMemeException(ErrorCode.NOT_FOUND_USER));

        FindPost findPost = findPostRepository.findById(findPostId)
                .orElseThrow(() -> new FindMyMemeException(ErrorCode.NOT_FOUND_FIND_POST));

        return new FindPostGetResponse(findPost, findPost.isOwner(user));
    }

}
