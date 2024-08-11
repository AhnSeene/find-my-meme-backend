package com.findmymeme.findpost.api;

import com.findmymeme.findpost.service.FindPostService;
import com.findmymeme.findpost.dto.FindPostUploadRequest;
import com.findmymeme.findpost.dto.FindPostUploadResponse;
import com.findmymeme.response.ApiResponse;
import com.findmymeme.response.ResponseUtil;
import com.findmymeme.response.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/find-posts")
public class FindPostController {

    private final FindPostService findPostService;

    @PostMapping
    public ResponseEntity<ApiResponse<FindPostUploadResponse>> upload(@RequestBody FindPostUploadRequest request) {
        return ResponseUtil.success(findPostService.upload(request, 1L), SuccessCode.FIND_POST_UPLOAD);
    }
}
