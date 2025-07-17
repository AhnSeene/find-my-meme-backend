package com.findmymeme.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "S3 업로드 완료 후 파일 메타데이터 저장 요청 DTO")
@Getter
@NoArgsConstructor
public class FileMetaRequest {

    @Schema(description = "업로드된 파일의 원본 이름", example = "my-meme.jpg", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{originalFilename.notblank}")
    private String originalFilename;

    @Schema(description = "S3에 업로드 완료 후 받은 Presigned URL. 서버는 이 URL을 파싱하여 파일 키(Key)를 추출합니다.", example = "https://find-my-meme.s3.ap-northeast-2.amazonaws.com/temps/1/asdf-qwer-1234.jpg?...", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{presignedUrl.notblank}")
    @Pattern(regexp = "^(https?|s3)://.*$", message = "{presignedUrl.pattern}")
    private String presignedUrl;

    @Schema(description = "이미지의 가로 크기 (px)", example = "800")
    @PositiveOrZero(message = "{width.positive}")
    private Integer width;

    @Schema(description = "이미지의 세로 크기 (px)", example = "600")
    @PositiveOrZero(message = "{height.positive}")
    private Integer height;

    @Schema(description = "파일 크기 (bytes)", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "{size.notnull}")
    @Positive(message = "{size.positive}")
    private Long size;

    @Builder
    public FileMetaRequest(String originalFilename, String presignedUrl, Integer width, Integer height, Long size) {
        this.originalFilename = originalFilename;
        this.presignedUrl = presignedUrl;
        this.width = width;
        this.height = height;
        this.size = size;
    }
}
