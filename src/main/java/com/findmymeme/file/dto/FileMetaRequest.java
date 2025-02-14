package com.findmymeme.file.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FileMetaRequest {

    @NotBlank(message = "{originalFilename.notblank}")
    private String originalFilename;

    @NotBlank(message = "{presignedUrl.notblank}")
    @Pattern(regexp = "^(https?|s3)://.*$", message = "{presignedUrl.pattern}")
    private String presignedUrl;

    @PositiveOrZero(message = "{width.positive}")
    private Integer width;

    @PositiveOrZero(message = "{height.positive}")
    private Integer height;

    @NotNull(message = "{size.notnull}")
    @Positive(message = "{size.positive}")
    private Long size;

    @Builder
    public FileMetaRequest(String originalFilename, String presignedUrl, int width, int height, Long size) {
        this.originalFilename = originalFilename;
        this.presignedUrl = presignedUrl;
        this.width = width;
        this.height = height;
        this.size = size;
    }
}
