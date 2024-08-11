package com.findmymeme.findpost.api;

import com.findmymeme.findpost.service.FindPostService;
import com.findmymeme.findpost.dto.FindPostUploadRequest;
import com.findmymeme.findpost.dto.FindPostUploadResponse;
import com.findmymeme.response.ApiResponse;
import com.findmymeme.response.ResponseUtil;
import com.findmymeme.response.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/find-posts")
public class FindPostController {

    private final FindPostService findPostService;

    @PostMapping
    public ResponseEntity<ApiResponse<FindPostUploadResponse>> upload(@RequestBody FindPostUploadRequest request, Long userId) {
        return ResponseUtil.success(findPostService.upload(request, userId), SuccessCode.FIND_POST_UPLOAD);
    }
}
