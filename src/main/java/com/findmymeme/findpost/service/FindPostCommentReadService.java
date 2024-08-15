package com.findmymeme.findpost.service;

import com.findmymeme.exception.ErrorCode;
import com.findmymeme.exception.FindMyMemeException;
import com.findmymeme.findpost.domain.FindPost;
import com.findmymeme.findpost.domain.FindPostComment;
import com.findmymeme.findpost.dto.*;
import com.findmymeme.findpost.repository.FindPostCommentRepository;
import com.findmymeme.findpost.repository.FindPostRepository;
import com.findmymeme.user.User;
import com.findmymeme.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FindPostCommentReadService {

    private final UserRepository userRepository;
    private final FindPostRepository findPostRepository;
    private final FindPostCommentRepository commentRepository;

    public FindPostCommentGetResponse getComment(Long findPostId, Long commentId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new FindMyMemeException(ErrorCode.NOT_FOUND_USER));

        FindPostComment comment = commentRepository.findWithUserById(commentId)
                .orElseThrow(() -> new FindMyMemeException(ErrorCode.NOT_FOUND_FIND_POST_COMMENT));

        return new FindPostCommentGetResponse(comment, comment.isOwner(user));
    }

    public List<FindPostCommentSummaryResponse> getCommentsWithReplys(Long postId) {
        FindPost findPost = findPostRepository.findWithUserById(postId)
                .orElseThrow(() -> new FindMyMemeException(ErrorCode.NOT_FOUND_FIND_POST));

        Map<Long, FindPostCommentSummaryResponse> commentMaps = new HashMap<>();
        List<FindPostCommentSummaryResponse> response = new ArrayList<>();

        //TODO 게시글 상태확인하기
        List<FindPostComment> comments = commentRepository.findAllCommentsAndReplies(postId);
        for (FindPostComment comment : comments) {
            if (comment.getParentComment() == null) {
                FindPostCommentSummaryResponse commentSummaryResponse = new FindPostCommentSummaryResponse(comment);
                commentMaps.put(comment.getId(), commentSummaryResponse);
                response.add(commentSummaryResponse);
            } else {
                commentMaps.get(comment.getParentComment().getId())
                                .addReply(comment);
            }
        }
        return response;
    }


}
