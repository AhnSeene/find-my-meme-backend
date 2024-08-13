package com.findmymeme.findpost.api;

import com.findmymeme.findpost.dto.FindPostGetResponse;
import com.findmymeme.findpost.dto.FindPostSummaryResponse;
import com.findmymeme.findpost.dto.FindPostUploadResponse;
import com.findmymeme.findpost.service.FindPostManageService;
import com.findmymeme.findpost.service.FindPostUploadService;
import com.findmymeme.findpost.dto.FindPostUploadRequest;
import com.findmymeme.response.ApiResponse;
import com.findmymeme.response.MyPage;
import com.findmymeme.response.ResponseUtil;
import com.findmymeme.response.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/find-posts")
public class FindPostController {

    private final FindPostUploadService findPostService;
    private final FindPostManageService findPostManageService;

    @PostMapping
    public ResponseEntity<ApiResponse<FindPostUploadResponse>> upload(
            @RequestBody FindPostUploadRequest request
    ) {
        return ResponseUtil.success(findPostService.upload(request, 1L),
                SuccessCode.FIND_POST_UPLOAD);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FindPostGetResponse>> getFindPost(
            @PathVariable("id") Long findPostId
    ) {
        return ResponseUtil.success(findPostManageService.getFindPost(findPostId, 1L),
                SuccessCode.FIND_POST_GET);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<MyPage<FindPostSummaryResponse>>> getFindPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseUtil.success(
                new MyPage<>(findPostManageService.getFindPosts(page, size))
                , SuccessCode.FIND_POST_GET);
    }
}
