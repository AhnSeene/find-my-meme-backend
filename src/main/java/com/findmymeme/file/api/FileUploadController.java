package com.findmymeme.file.api;

import com.findmymeme.common.resolver.CurrentUserId;
import com.findmymeme.exception.ErrorCode;
import com.findmymeme.file.dto.FileUploadResponse;
import com.findmymeme.file.service.FileService;
import com.findmymeme.response.ApiResponse;
import com.findmymeme.response.ResponseUtil;
import com.findmymeme.response.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/files")
public class FileUploadController {

    private final FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<FileUploadResponse>> upload(
            @RequestPart("file") MultipartFile file,
            @CurrentUserId Long userId
    ) {
        if (file.isEmpty()) {
            return ResponseUtil.error(null, ErrorCode.INVALID_INPUT_VALUE);
        }
        return ResponseUtil.success(fileService.uploadFile(file, userId), SuccessCode.FILE_UPLOAD);
    }
}
