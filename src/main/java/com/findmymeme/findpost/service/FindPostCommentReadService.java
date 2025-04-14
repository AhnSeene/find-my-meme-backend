package com.findmymeme.findpost.service;

import com.findmymeme.exception.ErrorCode;
import com.findmymeme.exception.FindMyMemeException;
import com.findmymeme.findpost.domain.FindPost;
import com.findmymeme.findpost.domain.FindPostComment;
import com.findmymeme.findpost.dto.*;
import com.findmymeme.findpost.repository.FindPostCommentRepository;
import com.findmymeme.findpost.repository.FindPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FindPostCommentReadService {

    private final FindPostCommentRepository commentRepository;
    private final FindPostRepository findPostRepository;

    public FindPostCommentGetResponse getComment(Long findPostId, Long commentId, Optional<Long> userId) {
        FindPost findPost = getFindPostById(findPostId);
        validateFindPostStatus(findPost);
        FindPostComment comment = commentRepository.findWithUserById(commentId)
                .orElseThrow(() -> new FindMyMemeException(ErrorCode.NOT_FOUND_FIND_POST_COMMENT));

        validateFindPost(comment, findPostId);
        boolean isAuthor = checkIfUserIsAuthor(comment, userId);
        return new FindPostCommentGetResponse(comment, isAuthor);
    }

    private void validateFindPost(FindPostComment comment, Long findPostId) {
        if (comment.isNotOfPost(findPostId)) {
            throw new FindMyMemeException(ErrorCode.REQUEST_INVALID_COMMENT_POST_RELATION);
        }
    }

    private boolean checkIfUserIsAuthor(FindPostComment findPostComment, Optional<Long> userId) {
        if (userId.isEmpty()) {
            return false;
        }
        return findPostComment.isAuthor(userId.get());
    }

    public List<FindPostCommentSummaryResponse> getCommentsWithReplys(Long findPostId) {
        FindPost findPost = getFindPostById(findPostId);
        validateFindPostStatus(findPost);

        Map<Long, FindPostCommentSummaryResponse> commentMaps = new HashMap<>();
        List<FindPostCommentSummaryResponse> response = new ArrayList<>();

        List<FindPostComment> comments = commentRepository.findAllCommentsAndReplies(findPostId);
        for (FindPostComment comment : comments) {
            FindPostCommentSummaryResponse commentSummaryResponse = new FindPostCommentSummaryResponse(comment);
            if (comment.getParentComment() == null) {
                response.add(commentSummaryResponse);
            } else {
                commentMaps.get(comment.getParentComment().getId())
                                .addReply(commentSummaryResponse);
            }
            commentMaps.put(comment.getId(), commentSummaryResponse);
        }
        return response;
    }

    private void validateFindPostStatus(FindPost findPost) {
        if (findPost.isDeleted()) {
            throw new FindMyMemeException(ErrorCode.REQUEST_CANNOT_WRITE_COMMENT_ON_DELETED_POST);
        }
    }

    private FindPost getFindPostById(Long findPostId) {
        return findPostRepository.findById(findPostId)
                .orElseThrow(() -> new FindMyMemeException(ErrorCode.NOT_FOUND_FIND_POST));
    }
}
