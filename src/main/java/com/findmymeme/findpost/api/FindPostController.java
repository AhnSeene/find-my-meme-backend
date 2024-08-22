package com.findmymeme.findpost.api;

import com.findmymeme.findpost.dto.*;
import com.findmymeme.findpost.service.FindPostReadService;
import com.findmymeme.findpost.service.FindPostWriteService;
import com.findmymeme.response.ApiResponse;
import com.findmymeme.response.MyPage;
import com.findmymeme.response.ResponseUtil;
import com.findmymeme.response.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/find-posts")
public class FindPostController {

    private final FindPostWriteService findPostWriteService;
    private final FindPostReadService findPostReadService;

    @PostMapping
    public ResponseEntity<ApiResponse<FindPostUploadResponse>> uploadFindPost(
            @Valid @RequestBody FindPostUploadRequest request,
            Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());
        return ResponseUtil.success(findPostWriteService.uploadFindPost(request, userId),
                SuccessCode.FIND_POST_UPLOAD);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FindPostGetResponse>> getFindPost(
            @PathVariable("id") Long findPostId
    ) {
        return ResponseUtil.success(findPostReadService.getFindPost(findPostId, 1L),
                SuccessCode.FIND_POST_GET);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<MyPage<FindPostSummaryResponse>>> getFindPosts(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "5") int size
    ) {
        return ResponseUtil.success(
                new MyPage<>(findPostReadService.getFindPosts(page, size))
                , SuccessCode.FIND_POST_GET);
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MyPage<MyFindPostSummaryResponse>>> getMyFindPosts(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());
        return ResponseUtil.success(
                new MyPage<>(findPostReadService.getMyFindPosts(page, size, userId))
                , SuccessCode.FIND_POST_GET
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FindPostUpdateResponse>> updateFindPost(
            @PathVariable("id") Long findPostId,
            @Valid @RequestBody FindPostUpdateRequest request,
            Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());
        return ResponseUtil.success(findPostWriteService.updateFindPost(request, findPostId, userId),
                SuccessCode.FIND_POST_UPDATE);
    }

    @PostMapping("/{postId}/comments/{commentId}/select")
    public ResponseEntity<ApiResponse<FindPostFoundResponse>> found(
            @PathVariable("postId") Long findPostId,
            @PathVariable("commentId") Long commentId,
            Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());
        return ResponseUtil.success(findPostWriteService.selectComment(findPostId, commentId, userId),
                SuccessCode.FIND_POST_FOUND);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> softDelete(
            @PathVariable("postId") Long findPostId,
            Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());
        findPostWriteService.softDelete(findPostId, userId);
        return ResponseUtil.success(null,
                SuccessCode.FIND_POST_DELETE);
    }
}
