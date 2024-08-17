package com.findmymeme.memepost.api;

import com.findmymeme.memepost.service.MemePostService;
import com.findmymeme.memepost.dto.MemePostUploadRequest;
import com.findmymeme.memepost.dto.MemePostUploadResponse;
import com.findmymeme.response.ApiResponse;
import com.findmymeme.response.ResponseUtil;
import com.findmymeme.response.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
