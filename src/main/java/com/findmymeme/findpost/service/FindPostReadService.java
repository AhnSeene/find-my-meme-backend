package com.findmymeme.findpost.service;

import com.findmymeme.exception.ErrorCode;
import com.findmymeme.exception.FindMyMemeException;
import com.findmymeme.findpost.domain.FindPost;
import com.findmymeme.findpost.dto.FindPostGetResponse;
import com.findmymeme.findpost.dto.FindPostSummaryResponse;
import com.findmymeme.findpost.repository.FindPostRepository;
import com.findmymeme.tag.domain.PostType;
import com.findmymeme.tag.service.PostTagService;
import com.findmymeme.user.User;
import com.findmymeme.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.findmymeme.tag.domain.PostType.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FindPostReadService {

    private final UserRepository userRepository;
    private final FindPostRepository findPostRepository;
    private final PostTagService postTagService;

    public FindPostGetResponse getFindPost(Long findPostId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new FindMyMemeException(ErrorCode.NOT_FOUND_USER));

        FindPost findPost = findPostRepository.findWithUserById(findPostId)
                .orElseThrow(() -> new FindMyMemeException(ErrorCode.NOT_FOUND_FIND_POST));

        List<String> tagNames = postTagService.getTagNames(findPost.getId(), FIND_POST);
        return new FindPostGetResponse(findPost, findPost.isOwner(user), tagNames);
    }

    public Page<FindPostSummaryResponse> getFindPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return findPostRepository.findAll(pageable)
                .map(FindPostSummaryResponse::new);
    }


}
