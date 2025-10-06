package com.findmymeme.memepost.dto;

import com.findmymeme.memepost.domain.InvocationSource;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 이미지 처리 완료 후 SQS를 통해 전달되는 DTO
 */
@Getter
@ToString
@NoArgsConstructor
public class ImageCompletionDto {

    @NotNull(message = "memePostId는 필수입니다.")
    private Long memePostId;

    @NotNull(message = "userId는 필수입니다.")
    private Long userId;

    @NotBlank(message = "thumbnail288Url은 비어있을 수 없습니다.")
    private String thumbnail288Url;

    @NotBlank(message = "thumbnail657Url은 비어있을 수 없습니다.")
    private String thumbnail657Url;
    private String status;
    private InvocationSource invocationSource;
}