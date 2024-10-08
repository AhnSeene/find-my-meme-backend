package com.findmymeme.findpost.service;

import com.findmymeme.exception.ErrorCode;
import com.findmymeme.exception.FindMyMemeException;
import com.findmymeme.findpost.domain.FindPost;
import com.findmymeme.findpost.domain.FindStatus;
import com.findmymeme.findpost.dto.FindPostGetResponse;
import com.findmymeme.findpost.dto.FindPostSummaryResponse;
import com.findmymeme.findpost.dto.MyFindPostSummaryResponse;
import com.findmymeme.findpost.repository.FindPostRepository;
import com.findmymeme.tag.domain.Tag;
import com.findmymeme.tag.service.PostTagService;
import com.findmymeme.user.domain.User;
import com.findmymeme.user.repository.UserRepository;
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

    @Transactional
    public FindPostGetResponse getFindPost(Long findPostId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new FindMyMemeException(ErrorCode.NOT_FOUND_USER));

        FindPost findPost = findPostRepository.findWithUserById(findPostId)
                .orElseThrow(() -> new FindMyMemeException(ErrorCode.NOT_FOUND_FIND_POST));

        List<Tag> tags = postTagService.getTags(findPost.getId(), FIND_POST);

        findPost.incrementViewCount();
        return new FindPostGetResponse(findPost, findPost.isOwner(user), tags);
    }

    public Page<FindPostSummaryResponse> getFindPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return findPostRepository.findAll(pageable)
                .map(findPost ->
                        new FindPostSummaryResponse(findPost,
                                postTagService.getTagNames(findPost.getId(), FIND_POST)
                        )
                );
    }

    public Page<FindPostSummaryResponse> getFindPostsByFindStatus(int page, int size, FindStatus findStatus) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return findPostRepository.findAllByFindStatus(pageable, findStatus)
                .map(findPost ->
                        new FindPostSummaryResponse(findPost,
                                postTagService.getTagNames(findPost.getId(), FIND_POST)
                        )
                );
    }

    public Page<MyFindPostSummaryResponse> getFindPostsByAuthor(int page, int size, String authorName) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return findPostRepository.findAllByUsername(pageable, authorName)
                .map(findPost ->
                        new MyFindPostSummaryResponse(findPost,
                                postTagService.getTagNames(findPost.getId(), FIND_POST)
                        )
                );
    }

    public Page<MyFindPostSummaryResponse> getFindPostsByAuthorAndFindStatus(int page, int size, String authorName, FindStatus findStatus) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return findPostRepository.findAllByUsernameAndFindStatus(pageable, authorName, findStatus)
                .map(findPost ->
                        new MyFindPostSummaryResponse(findPost,
                                postTagService.getTagNames(findPost.getId(), FIND_POST)
                        )
                );
    }
}
