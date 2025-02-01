package com.findmymeme.memepost.api;

import com.findmymeme.common.resolver.CurrentUserId;
import com.findmymeme.memepost.domain.MemePostSort;
import com.findmymeme.memepost.dto.*;
import com.findmymeme.memepost.service.MemePostLikeService;
import com.findmymeme.memepost.service.MemePostService;
import com.findmymeme.response.ApiResponse;
import com.findmymeme.response.MySlice;
import com.findmymeme.response.ResponseUtil;
import com.findmymeme.response.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/meme-posts")
public class MemePostController {

    private static final String ATTACHMENT_FILENAME_TEMPLATE = "attachment; filename=\"%s\"";
    private final MemePostService memePostService;
    private final MemePostLikeService memePostLikeService;

    @PostMapping
    public ResponseEntity<ApiResponse<MemePostUploadResponse>> uploadMemePost(
            @Valid @RequestBody MemePostUploadRequest request,
            @CurrentUserId(required = false) Long userId
    ) {
        return ResponseUtil.success(memePostService.uploadMemePost(request, userId),
                SuccessCode.MEME_POST_UPLOAD
        );
    }

    @GetMapping("/{memePostId}")
    public ResponseEntity<ApiResponse<MemePostGetResponse>> getMemePost(
            @PathVariable("memePostId") Long memePostId,
            @CurrentUserId(required = false) Optional<Long> userId
    ) {
        return ResponseUtil.success(memePostService.getMemePost(memePostId, userId),
                SuccessCode.MEME_POST_GET
        );
    }

    @GetMapping("/{memePostId}/download")
    public ResponseEntity<Resource> downloadMemePost(
            @PathVariable("memePostId") Long memePostId
    ) {
        MemePostDownloadDto downloadDto = memePostService.download(memePostId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, String.format(ATTACHMENT_FILENAME_TEMPLATE, downloadDto.getFilename()))
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(downloadDto.getResource());
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<MySlice<MemePostSummaryResponse>>> searchMemePosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @ModelAttribute MemePostSearchCond searchCond,
            @CurrentUserId(required = false) Optional<Long> userId
    ) {
        Slice<MemePostSummaryResponse> responses = memePostService.getMemePostsWithLikeInfo(
                page, size, MemePostSort.CREATED, searchCond, userId
        );
        return ResponseUtil.success(new MySlice<>(responses), SuccessCode.MEME_POST_LIST);
    }

    @GetMapping("/{memePostId}/recommendations")
    public ResponseEntity<ApiResponse<List<MemePostSummaryResponse>>> getRecommendations(
            @PathVariable("memePostId") Long memePostId,
            @RequestParam(defaultValue = "20") int size,
            @CurrentUserId(required = false) Optional<Long> userId
    ) {
        List<MemePostSummaryResponse> responses = memePostService.getRecommendedPostsWithLikeInfo(memePostId, size, userId);
        return ResponseUtil.success(responses, SuccessCode.MEME_POST_LIST);
    }

    @PostMapping("/{memePostId}/toggleLike")
    public ResponseEntity<ApiResponse<MemePostLikeResponse>> toggleLikeMemePost(
            @PathVariable("memePostId") Long memePostId,
            @CurrentUserId Long userId
    ) {
        return ResponseUtil.success(memePostLikeService.toggleLike(memePostId, userId),
                SuccessCode.MEME_POST_LIKE
        );
    }

    @DeleteMapping("/{memePostId}")
    public ResponseEntity<ApiResponse<Void>> softDelete(
            @PathVariable("memePostId") Long memePostId,
            @CurrentUserId Long userId
    ) {
        memePostService.softDelete(memePostId, userId);
        return ResponseUtil.success(
                null,
                SuccessCode.MEME_POST_DELETE
        );
    }

    @GetMapping("/users/{authorName}")
    public ResponseEntity<ApiResponse<MemePostUserSummaryResponse>> getMemePostsByAuthorName(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable("authorName") String authorName,
            @CurrentUserId(required = false) Optional<Long> userId
    ) {
        return ResponseUtil.success(responses, SuccessCode.MEME_POST_AUTHOR_LIST);
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

    @GetMapping("/ranks/period")
    public ResponseEntity<ApiResponse<List<MemePostSummaryResponse>>> getRankedPostsWithPeriod(
            @RequestParam(name = "period") Period period,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        List<MemePostSummaryResponse> responses = memePostService.getRankedPostsWithPeriod(page, size, period);
        return ResponseUtil.success(responses, SuccessCode.MEME_POST_LIST);
    }

}
