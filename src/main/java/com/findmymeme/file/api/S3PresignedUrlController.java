package com.findmymeme.file.api;

import com.findmymeme.common.resolver.CurrentUserId;
import com.findmymeme.file.dto.FileMetaRequest;
import com.findmymeme.file.dto.FileUploadResponse;
import com.findmymeme.file.dto.PresignedUploadResponse;
import com.findmymeme.file.service.S3PresignedUrlService;
import com.findmymeme.response.ApiResponse;
import com.findmymeme.response.ResponseUtil;
import com.findmymeme.response.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@Profile("prod")
@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class S3PresignedUrlController {

    private final S3PresignedUrlService s3PresignedUrlService;

    @PostMapping("/presigned-upload")
    public ResponseEntity<ApiResponse<PresignedUploadResponse>> completeUpload(
            @RequestParam String filename,
            @CurrentUserId Long userId
    ) {
        return ResponseUtil.success(s3PresignedUrlService.generatePresignedUrl(filename, userId), SuccessCode.PRESIGNEDURL_UPLOAD);
    }

    @PostMapping("/upload-complete")
    public ResponseEntity<ApiResponse<FileUploadResponse>> completeUpload(
            @RequestBody @Valid FileMetaRequest fileMetaRequest,
            @CurrentUserId Long userId
    ) {
        return ResponseUtil.success(s3PresignedUrlService.saveFileMeta(fileMetaRequest, userId), SuccessCode.COMPLETE_FILE_UPLOAD);
    }

    @GetMapping("/presigned-download")
    public String getPresignedDownloadUrl(@RequestParam String filename) {
        return s3PresignedUrlService.generatePresignedDownloadUrl("uploads/" + filename);
    }
}
