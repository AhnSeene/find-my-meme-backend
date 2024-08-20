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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/meme-posts")
public class MemePostController {

    private final MemePostService memePostService;
    private final MemePostLikeService memePostLikeService;

    @PostMapping
    public ResponseEntity<ApiResponse<MemePostUploadResponse>> uploadMemePost(
            @Valid @RequestBody MemePostUploadRequest request
            ) {
        return ResponseUtil.success(memePostService.uploadMemePost(request, 1L),
                SuccessCode.FIND_POST_GET
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
                SuccessCode.MEME_POST_LIST
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
}
