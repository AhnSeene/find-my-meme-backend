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
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/find-posts")
public class FindPostController {

    private final FindPostWriteService findPostWriteService;
    private final FindPostReadService findPostReadService;

    @PostMapping
    public ResponseEntity<ApiResponse<FindPostUploadResponse>> uploadFindPost(
            @Valid @RequestBody FindPostUploadRequest request
    ) {
        return ResponseUtil.success(findPostWriteService.uploadFindPost(request, 1L),
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

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FindPostUpdateResponse>> updateFindPost(
            @PathVariable("id") Long findPostId,
            @Valid @RequestBody FindPostUpdateRequest request
    ) {
        return ResponseUtil.success(findPostWriteService.updateFindPost(request, findPostId, 1L),
                SuccessCode.FIND_POST_UPDATE);
    }

}
