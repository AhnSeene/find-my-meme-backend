package com.findmymeme.file.api;

import com.findmymeme.common.resolver.CurrentUserId;
import com.findmymeme.file.dto.FileMetaRequest;
import com.findmymeme.file.dto.FileUploadResponse;
import com.findmymeme.file.service.S3PresignedUrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class S3PresignedUrlController {

    private final S3PresignedUrlService s3PresignedUrlService;

    @PostMapping("/presigned-upload")
    public String completeUpload(
            @RequestParam String filename,
            @CurrentUserId Long userId
    ) {
        return s3PresignedUrlService.generatePresignedUrl(filename, userId);
    }

