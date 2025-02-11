package com.findmymeme.findpost.service;

import com.findmymeme.exception.ErrorCode;
import com.findmymeme.exception.FindMyMemeException;
import com.findmymeme.findpost.domain.FindPost;
import com.findmymeme.findpost.domain.FindStatus;
import com.findmymeme.findpost.dto.FindPostGetResponse;
import com.findmymeme.findpost.dto.FindPostSummaryResponse;
import com.findmymeme.findpost.dto.MyFindPostSummaryResponse;
import com.findmymeme.findpost.repository.FindPostRepository;
import com.findmymeme.tag.service.FindPostTagService;
import com.findmymeme.user.domain.User;
import com.findmymeme.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FindPostReadService {

    private final UserRepository userRepository;
    private final FindPostRepository findPostRepository;
    private final FindPostTagService findPostTagService;

    @Transactional
    public FindPostGetResponse getFindPost(Long findPostId, Optional<Long> userId) {
        FindPost findPost = findPostRepository.findDetailsById(findPostId)
                .orElseThrow(() -> new FindMyMemeException(ErrorCode.NOT_FOUND_FIND_POST));

        findPost.incrementViewCount();

        boolean isOwner = checkIfUserIsAuthor(userId, findPost);
        return new FindPostGetResponse(findPost, isOwner, findPost.getTags());
    }

    private boolean checkIfUserIsAuthor(Optional<Long> userId, FindPost findPost) {
        if (userId.isEmpty()) {
            return false;
        }
        User user = userRepository.findById(userId.get())
                .orElseThrow(() -> new FindMyMemeException(ErrorCode.NOT_FOUND_USER));

        return findPost.isAuthor(user);
    }

    public Page<FindPostSummaryResponse> getFindPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return findPostRepository.findAll(pageable)
                .map(findPost ->
                        new FindPostSummaryResponse(findPost,
                                findPostTagService.getTagNames(findPost.getId())
                        )
                );
    }

    public Page<FindPostSummaryResponse> getFindPostsByFindStatus(int page, int size, FindStatus findStatus) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return findPostRepository.findAllByFindStatus(pageable, findStatus)
                .map(findPost ->
                        new FindPostSummaryResponse(findPost,
                                findPostTagService.getTagNames(findPost.getId())
                        )
                );
    }

    public Page<MyFindPostSummaryResponse> getFindPostsByAuthor(int page, int size, String authorName) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return findPostRepository.findAllByUsername(pageable, authorName)
                .map(findPost ->
                        new MyFindPostSummaryResponse(findPost,
                                findPostTagService.getTagNames(findPost.getId())
                        )
                );
    }

    public Page<MyFindPostSummaryResponse> getFindPostsByAuthorAndFindStatus(int page, int size, String authorName, FindStatus findStatus) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return findPostRepository.findAllByUsernameAndFindStatus(pageable, authorName, findStatus)
                .map(findPost ->
                        new MyFindPostSummaryResponse(findPost,
                                findPostTagService.getTagNames(findPost.getId())
                        )
                );
    }
}
