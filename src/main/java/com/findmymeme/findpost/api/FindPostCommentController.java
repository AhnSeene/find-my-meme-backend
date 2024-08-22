package com.findmymeme.findpost.api;

import com.findmymeme.findpost.dto.*;
import com.findmymeme.findpost.service.FindPostCommentReadService;
import com.findmymeme.findpost.service.FindPostCommentWriteService;
import com.findmymeme.response.ApiResponse;
import com.findmymeme.response.ResponseUtil;
import com.findmymeme.response.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/find-posts/{postId}/comments")
public class FindPostCommentController {

    private final FindPostCommentWriteService commentWriteService;
    private final FindPostCommentReadService commentReadService;

    @PostMapping
    public ResponseEntity<ApiResponse<FindPostCommentAddResponse>> addComment(
            @PathVariable("postId") Long postId,
            @Valid @RequestBody FindPostCommentAddRequest request,
            Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());
        return ResponseUtil.success(
                commentWriteService.addComment(request, postId, userId),
                SuccessCode.FIND_POST_COMMENT_UPLOAD
        );
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<ApiResponse<FindPostCommentGetResponse>> getComment(
            @PathVariable("postId") Long postId,
            @PathVariable("commentId") Long commentId,
            Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());
        return ResponseUtil.success(
                commentReadService.getComment(postId, commentId, userId),
                SuccessCode.FIND_POST_COMMENT_GET
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<FindPostCommentSummaryResponse>>> getComments(
            @PathVariable("postId") Long postId
    ) {
        return ResponseUtil.success(
                commentReadService.getCommentsWithReplys(postId),
                SuccessCode.FIND_POST_COMMENT_LIST
        );
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<ApiResponse<FindPostCommentUpdateResponse>> updateComment(
            @PathVariable("postId") Long postId,
            @PathVariable("commentId") Long commentId,
            @Valid @RequestBody FindPostCommentUpdateRequest request,
            Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());
        return ResponseUtil.success(
                commentWriteService.updateComment(request, postId, commentId, userId),
                SuccessCode.FIND_POST_COMMENT_UPDATE
        );
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<FindPostCommentDeleteResponse>> softDelete(
            @PathVariable("postId") Long postId,
            @PathVariable("commentId") Long commentId,
            Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());
        return ResponseUtil.success(
                commentWriteService.softDelete(postId, commentId, userId),
                SuccessCode.FIND_POST_COMMENT_DELETE
        );
    }
}
