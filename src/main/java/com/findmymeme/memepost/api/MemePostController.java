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
            @CurrentUserId Long userId
    ) {
        return ResponseUtil.success(memePostService.uploadMemePost(request, userId),
                SuccessCode.MEME_POST_UPLOAD
        );
    }

    @GetMapping("/{memePostId}")
    public ResponseEntity<ApiResponse<MemePostGetResponse>> getMemePost(
            @PathVariable("memePostId") Long memePostId,
            @CurrentUserId Optional<Long> userId
    ) {
        return ResponseUtil.success(memePostService.getMemePost(memePostId, userId),
                SuccessCode.MEME_POST_GET
        );
    }

    @GetMapping("/{memePostId}/2")
    public ResponseEntity<ApiResponse<MemePostGetResponse>> getMemePost2(
            @PathVariable("memePostId") Long memePostId,
            @CurrentUserId Optional<Long> userId
    ) {
        return ResponseUtil.success(memePostService.getMemePost2(memePostId, userId),
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

    @GetMapping
    public ResponseEntity<ApiResponse<MySlice<MemePostSummaryResponse>>> getMemePosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "LIKES") MemePostSort sort,
            @CurrentUserId Optional<Long> userId
    ) {
        Slice<MemePostSummaryResponse> responses = memePostService.getMemePosts(page, size, sort, userId);
        return ResponseUtil.success(new MySlice<>(responses), SuccessCode.MEME_POST_LIST);
    }

    @GetMapping("list/1")
    public ResponseEntity<ApiResponse<MySlice<MemePostSummaryResponse>>> getMemePosts1(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "LIKES") MemePostSort sort,
            @CurrentUserId Optional<Long> userId
    ) {
        Slice<MemePostSummaryResponse> responses = memePostService.getMemePosts1(page, size, sort, userId);
        return ResponseUtil.success(new MySlice<>(responses), SuccessCode.MEME_POST_LIST);
    }

    @GetMapping("list/2")
    public ResponseEntity<ApiResponse<MySlice<MemePostSummaryResponse>>> getMemePosts2(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "LIKES") MemePostSort sort,
            @CurrentUserId Optional<Long> userId
    ) {
        Slice<MemePostSummaryResponse> responses = memePostService.getMemePosts2(page, size, sort, userId);
        return ResponseUtil.success(new MySlice<>(responses), SuccessCode.MEME_POST_LIST);
    }

    @GetMapping("list/3")
    public ResponseEntity<ApiResponse<MySlice<MemePostSummaryResponse>>> getMemePosts3(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "LIKES") MemePostSort sort,
            @CurrentUserId Optional<Long> userId
    ) {
        Slice<MemePostSummaryResponse> responses = memePostService.getMemePosts3(page, size, sort, userId);
        return ResponseUtil.success(new MySlice<>(responses), SuccessCode.MEME_POST_LIST);
    }

    @GetMapping("list/4")
    public ResponseEntity<ApiResponse<MySlice<MemePostSummaryResponse>>> getMemePosts4(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "LIKES") MemePostSort sort,
            @CurrentUserId Optional<Long> userId
    ) {
        Slice<MemePostSummaryResponse> responses = memePostService.getMemePosts4(page, size, sort, userId);
        return ResponseUtil.success(new MySlice<>(responses), SuccessCode.MEME_POST_LIST);
    }

    @GetMapping("list/5")
    public ResponseEntity<ApiResponse<MySlice<MemePostSummaryResponse>>> getMemePosts5(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "LIKES") MemePostSort sort,
            @CurrentUserId Optional<Long> userId
    ) {
        Slice<MemePostSummaryResponse> responses = memePostService.getMemePosts5(page, size, sort, userId);
        return ResponseUtil.success(new MySlice<>(responses), SuccessCode.MEME_POST_LIST);
    }

    @GetMapping("list/6")
    public ResponseEntity<ApiResponse<MySlice<MemePostSummaryResponse>>> getMemePosts6(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "LIKES") MemePostSort sort,
            @CurrentUserId Optional<Long> userId
    ) {
        Slice<MemePostSummaryResponse> responses = memePostService.getMemePosts6(page, size, sort, userId);
        return ResponseUtil.success(new MySlice<>(responses), SuccessCode.MEME_POST_LIST);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<MySlice<MemePostSummaryResponse>>> searchMemePosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @ModelAttribute MemePostSearchCond searchCond,
            @CurrentUserId Optional<Long> userId
    ) {
        Slice<MemePostSummaryResponse> responses = memePostService.searchMemePosts(page, size, searchCond, userId);
        return ResponseUtil.success(new MySlice<>(responses), SuccessCode.MEME_POST_LIST);
    }

    @GetMapping("/{memePostId}/recommendations")
    public ResponseEntity<ApiResponse<List<MemePostSummaryResponse>>> getRecommendations(
            @PathVariable("memePostId") Long memePostId,
            @RequestParam(defaultValue = "20") int size,
            @CurrentUserId Optional<Long> userId
    ) {
        List<MemePostSummaryResponse> responses = memePostService.getRecommendedPosts(memePostId, size, userId);
        return ResponseUtil.success(responses, SuccessCode.MEME_POST_LIST);
    }

    @GetMapping("/{memePostId}/recommendations/2")
    public ResponseEntity<ApiResponse<List<MemePostSummaryResponse>>> getRecommendations2(
            @PathVariable("memePostId") Long memePostId,
            @RequestParam(defaultValue = "20") int size,
            @CurrentUserId Optional<Long> userId
    ) {
        List<MemePostSummaryResponse> responses = memePostService.getRecommendedPosts2(memePostId, size, userId);
        return ResponseUtil.success(responses, SuccessCode.MEME_POST_LIST);
    }

    @GetMapping("/{memePostId}/recommendations/3")
    public ResponseEntity<ApiResponse<List<MemePostSummaryResponse>>> getRecommendations3(
            @PathVariable("memePostId") Long memePostId,
            @RequestParam(defaultValue = "20") int size,
            @CurrentUserId Optional<Long> userId
    ) {
        List<MemePostSummaryResponse> responses = memePostService.getRecommendedPosts3(memePostId, size, userId);
        return ResponseUtil.success(responses, SuccessCode.MEME_POST_LIST);
    }

    @GetMapping("/{memePostId}/recommendations/4")
    public ResponseEntity<ApiResponse<List<MemePostSummaryResponse>>> getRecommendations4(
            @PathVariable("memePostId") Long memePostId,
            @RequestParam(defaultValue = "20") int size,
            @CurrentUserId Optional<Long> userId
    ) {
        List<MemePostSummaryResponse> responses = memePostService.getRecommendedPosts4(memePostId, size, userId);
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
            @CurrentUserId Optional<Long> userId
    ) {
        MemePostUserSummaryResponse responses = memePostService.getMemePostsByAuthorName(page, size, authorName, userId);
        return ResponseUtil.success(responses, SuccessCode.MEME_POST_AUTHOR_LIST);
    }

    @GetMapping("/users/{authorName}/2")
    public ResponseEntity<ApiResponse<MemePostUserSummaryResponse>> getMemePostsByAuthorName2(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable("authorName") String authorName,
            @CurrentUserId Optional<Long> userId
    ) {
        MemePostUserSummaryResponse responses = memePostService.getMemePostsByAuthorName2(page, size, authorName, userId);
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
