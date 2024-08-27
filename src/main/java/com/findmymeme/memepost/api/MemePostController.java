package com.findmymeme.memepost.api;

import com.findmymeme.memepost.dto.*;
import com.findmymeme.memepost.service.MemePostLikeService;
import com.findmymeme.memepost.service.MemePostService;
import com.findmymeme.response.ApiResponse;
import com.findmymeme.response.MySlice;
import com.findmymeme.response.ResponseUtil;
import com.findmymeme.response.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/meme-posts")
public class MemePostController {

    private final MemePostService memePostService;
    private final MemePostLikeService memePostLikeService;

    @PostMapping
    public ResponseEntity<ApiResponse<MemePostUploadResponse>> uploadMemePost(
            @Valid @RequestBody MemePostUploadRequest request,
            Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());
        return ResponseUtil.success(memePostService.uploadMemePost(request, userId),
                SuccessCode.MEME_POST_UPLOAD
        );
    }

    @GetMapping("/{memePostId}")
    public ResponseEntity<ApiResponse<MemePostGetResponse>> getMemePost(
            @PathVariable("memePostId") Long memePostId,
            Authentication authentication
    ) {
        MemePostGetResponse responses = null;
        if (authentication == null) {
            responses = memePostService.getMemePost(memePostId);
        } else {
            Long userId = Long.parseLong(authentication.getName());
            responses = memePostService.getMemePost(memePostId, userId);
        }
        return ResponseUtil.success(
                responses,
                SuccessCode.MEME_POST_GET
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<MySlice<MemePostSummaryResponse>>> getMemePosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication
    ) {
        Slice<MemePostSummaryResponse> responses = null;
        if (authentication == null) {
            responses = memePostService.getMemePosts(page, size);
        } else {
            Long userId = Long.parseLong(authentication.getName());
            responses = memePostService.getMemePosts(page, size, userId);
        }
        return ResponseUtil.success(
                new MySlice<>(responses),
                SuccessCode.MEME_POST_LIST
        );
    }

    @GetMapping("/{memePostId}/recommendations")
    public ResponseEntity<ApiResponse<List<MemePostSummaryResponse>>> getMemePosts(
            @PathVariable("memePostId") Long memePostId,
            Authentication authentication
    ) {
        List<MemePostSummaryResponse> responses = null;
        if (authentication == null) {
            responses = memePostService.getRecommendedPosts(memePostId);
        } else {
            Long userId = Long.parseLong(authentication.getName());
            responses = memePostService.getRecommendedPosts(memePostId, userId);
        }
        return ResponseUtil.success(
                responses, SuccessCode.MEME_POST_LIST
        );
    }

    @PostMapping("/{memePostId}/toggleLike")
    public ResponseEntity<ApiResponse<MemePostLikeResponse>> toggleLikeMemePost(
            @PathVariable("memePostId") Long memePostId,
            Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());
        return ResponseUtil.success(
                memePostLikeService.toggleLike(memePostId, userId),
                SuccessCode.MEME_POST_LIKE
        );
    }

    @DeleteMapping("/{memePostId}")
    public ResponseEntity<ApiResponse<Void>> softDelete(
            @PathVariable("memePostId") Long memePostId,
            Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());
        memePostService.softDelete(memePostId, userId);
        return ResponseUtil.success(
                null,
                SuccessCode.MEME_POST_DELETE
        );
    }

    @GetMapping("/users/{authorName}")
    public ResponseEntity<ApiResponse<MemePostUserSummaryResponse>> getMemePostsByAuthorId(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable("authorName") String authorName,
            Authentication authentication
    ) {
        MemePostUserSummaryResponse responses = null;
        if (authentication == null) {
            responses = memePostService.getMemePostsByAuthorId(page, size, authorName);
        } else {
            Long userId = Long.parseLong(authentication.getName());
            responses =  memePostService.getMemePostsByAuthorId(page, size, authorName, userId);
        }
        return ResponseUtil.success(
                responses,
                SuccessCode.MEME_POST_AUTHOR_LIST
        );
    }

    @GetMapping("/ranks/all")
    public ResponseEntity<ApiResponse<List<MemePostSummaryResponse>>> getRankedPostsAllPeriod(
            @RequestParam(name = "sort", defaultValue = "LIKE") Sort sort,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        List<MemePostSummaryResponse> responses = memePostService.getRankedPostsAllPeriod(page, size, sort);
        return ResponseUtil.success(responses, SuccessCode.MEME_POST_LIST);
    }


}
