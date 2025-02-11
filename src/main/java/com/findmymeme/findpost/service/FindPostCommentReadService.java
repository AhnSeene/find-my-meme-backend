package com.findmymeme.findpost.service;

import com.findmymeme.exception.ErrorCode;
import com.findmymeme.exception.FindMyMemeException;
import com.findmymeme.findpost.domain.FindPost;
import com.findmymeme.findpost.domain.FindPostComment;
import com.findmymeme.findpost.dto.*;
import com.findmymeme.findpost.repository.FindPostCommentRepository;
import com.findmymeme.findpost.repository.FindPostRepository;
import com.findmymeme.user.domain.User;
import com.findmymeme.user.repository.UserRepository;
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

    private final UserRepository userRepository;
    private final FindPostRepository findPostRepository;
    private final FindPostCommentRepository commentRepository;


    public FindPostCommentGetResponse getComment(Long findPostId, Long commentId, Optional<Long> userId) {
        FindPostComment comment = commentRepository.findWithUserById(commentId)
                .orElseThrow(() -> new FindMyMemeException(ErrorCode.NOT_FOUND_FIND_POST_COMMENT));

        validateFindPost(comment, findPostId);

        boolean isAuthor = checkIfUserIsAuthor(userId, comment);

        return new FindPostCommentGetResponse(comment, isAuthor);
    }

    private void validateFindPost(FindPostComment comment, Long findPostId) {
        if (!comment.belongsToFindPost(findPostId)) {
            throw new FindMyMemeException(ErrorCode.COMMENT_NOT_BELONG_TO_POST);
        }
    }

    private boolean checkIfUserIsAuthor(Optional<Long> userId, FindPostComment findPostComment) {
        if (userId.isEmpty()) {
            return false;
        }
        return findPostComment.isAuthor(userId.get());
    }

    public List<FindPostCommentSummaryResponse> getCommentsWithReplys(Long findPostId) {
        Map<Long, FindPostCommentSummaryResponse> commentMaps = new HashMap<>();
        List<FindPostCommentSummaryResponse> response = new ArrayList<>();

        //TODO 게시글 상태확인하기
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


}
