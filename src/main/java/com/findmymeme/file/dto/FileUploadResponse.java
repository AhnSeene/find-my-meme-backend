package com.findmymeme.file.dto;

import com.findmymeme.file.domain.FileMeta;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "파일 업로드 완료 응답 DTO")
@Getter
public class FileUploadResponse {

    @Schema(description = "업로드된 파일의 원본 이름", example = "my-meme.jpg")
    private String originalFilename;
    @Schema(description = "서버에 저장된 파일의 URL (S3 Object Key). 이 URL을 다른 API(ex. 밈 게시물 업로드)에서 사용합니다.", example = "temps/1/asdf-qwer-1234.jpg")
    private String fileUrl;

    public FileUploadResponse(FileMeta fileMeta) {
        this.originalFilename = fileMeta.getOriginalFilename();
        this.fileUrl = fileMeta.getFileUrl();
    }

}
