package com.findmymeme.file.api;

import com.findmymeme.common.resolver.CurrentUserId;
import com.findmymeme.file.dto.FileMetaRequest;
import com.findmymeme.file.dto.FileUploadResponse;
import com.findmymeme.file.dto.PresignedUploadResponse;
import com.findmymeme.file.service.S3PresignedUrlService;
import com.findmymeme.response.ApiResult;
import com.findmymeme.response.ResponseUtil;
import com.findmymeme.response.SuccessCode;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "7. Files")
@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class S3PresignedUrlController {

    private final S3PresignedUrlService s3PresignedUrlService;

    @Operation(summary = "파일 업로드용 Presigned URL 생성", description = "S3에 직접 파일을 업로드할 수 있는 임시 URL(Presigned URL)을 생성합니다. 이 URL로 파일을 PUT 요청으로 보내야 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Presigned URL 생성 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content(schema = @Schema(implementation = ApiResult.class)))
    })
    @PostMapping("/presigned-upload")
    public ResponseEntity<ApiResult<PresignedUploadResponse>> generatePresignedUrl(
            @Parameter(description = "업로드할 원본 파일 이름", example = "my-meme.jpg") @RequestParam String filename,
            @Parameter(hidden = true) @CurrentUserId Long userId
    ) {
        return ResponseUtil.success(s3PresignedUrlService.generatePresignedUrl(filename, userId), SuccessCode.PRESIGNEDURL_UPLOAD);
    }

    @Operation(summary = "파일 업로드 완료 처리", description = "클라이언트가 Presigned URL을 통해 S3에 파일 업로드를 완료한 후, 서버에 파일 메타데이터를 저장하도록 요청합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "파일 메타데이터 저장 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 유효성 검사 실패", content = @Content(schema = @Schema(implementation = ApiResult.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content(schema = @Schema(implementation = ApiResult.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 임시 파일 정보", content = @Content(schema = @Schema(implementation = ApiResult.class)))
    })
    @PostMapping("/upload-complete")
    public ResponseEntity<ApiResult<FileUploadResponse>> completeUpload(
            @RequestBody @Valid FileMetaRequest fileMetaRequest,
            @Parameter(hidden = true) @CurrentUserId Long userId
    ) {
        return ResponseUtil.success(s3PresignedUrlService.saveFileMeta(fileMetaRequest, userId), SuccessCode.COMPLETE_FILE_UPLOAD);
    }

    @Operation(summary = "파일 다운로드용 Presigned URL 생성", description = "S3에 저장된 파일을 다운로드할 수 있는 임시 URL(Presigned URL)을 생성합니다.")
    @GetMapping("/presigned-download")
    public String getPresignedDownloadUrl(@RequestParam String filename,
                                          @Parameter(hidden = true) @CurrentUserId Long userId) {
        return s3PresignedUrlService.generatePresignedDownloadUrl("uploads/" + filename);
    }
}
