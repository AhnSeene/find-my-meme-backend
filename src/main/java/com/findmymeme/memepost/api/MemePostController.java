package com.findmymeme.memepost.api;

import com.findmymeme.memepost.dto.MemePostGetResponse;
import com.findmymeme.memepost.dto.MemePostSummaryResponse;
import com.findmymeme.memepost.service.MemePostService;
import com.findmymeme.memepost.dto.MemePostUploadRequest;
import com.findmymeme.memepost.dto.MemePostUploadResponse;
import com.findmymeme.response.ApiResponse;
import com.findmymeme.response.MySlice;
import com.findmymeme.response.ResponseUtil;
import com.findmymeme.response.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/meme-posts")
public class MemePostController {

    private final MemePostService memePostService;

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
            @PathVariable("memePostId") Long memePostId
    ) {
        return ResponseUtil.success(
                memePostService.getMemePost(memePostId, 1L),
                SuccessCode.MEME_POST_LIST
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<MySlice<MemePostSummaryResponse>>> getMemePosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseUtil.success(
                new MySlice<>(memePostService.getMemePosts(page, size)),
                SuccessCode.MEME_POST_LIST
        );
    }
}