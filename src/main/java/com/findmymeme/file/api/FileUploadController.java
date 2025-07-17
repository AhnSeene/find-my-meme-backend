package com.findmymeme.file.api;

import com.findmymeme.common.resolver.CurrentUserId;
import com.findmymeme.exception.ErrorCode;
import com.findmymeme.file.dto.FileUploadResponse;
import com.findmymeme.file.service.FileService;
import com.findmymeme.response.ApiResponse;
import com.findmymeme.response.ResponseUtil;
import com.findmymeme.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

//@Tag(name = "7. File Management", description = "파일 업로드 및 관리 관련 API")
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/v1/files")
//public class FileUploadController {
//
//    private final FileService fileService;
//
//    @Operation(summary = "파일 직접 업로드 (임시 저장)", description = "서버로 직접 파일을 전송하여 임시 저장소에 저장합니다. (Presigned URL 방식과 다른 레거시/내부용 API일 수 있습니다.)")
//    @PostMapping("/upload")
//    public ResponseEntity<ApiResponse<FileUploadResponse>> upload(
//            @RequestPart("file") MultipartFile file,
//            @Parameter(hidden = true) @CurrentUserId Long userId
//    ) {
//        if (file.isEmpty()) {
//            return ResponseUtil.error(null, ErrorCode.REQUEST_INVALID_INPUT);
//        }
//        return ResponseUtil.success(fileService.uploadFile(file, userId), SuccessCode.FILE_UPLOAD);
//    }
//}
