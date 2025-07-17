package com.findmymeme.file.api;

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
//    public ResponseEntity<ApiResult<FileUploadResponse>> upload(
//            @RequestPart("file") MultipartFile file,
//            @Parameter(hidden = true) @CurrentUserId Long userId
//    ) {
//        if (file.isEmpty()) {
//            return ResponseUtil.error(null, ErrorCode.REQUEST_INVALID_INPUT);
//        }
//        return ResponseUtil.success(fileService.uploadFile(file, userId), SuccessCode.FILE_UPLOAD);
//    }
//}
