package com.findmymeme.memepost.dto;

import com.findmymeme.memepost.domain.MediaType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Getter
@Schema(description = "밈 게시물 검색 조건 DTO")
public class MemePostSearchCond {

    @Schema(description = "미디어 타입으로 필터링. **가능한 값: STATIC, ANIMATED**", example = "STATIC")
    private MediaType mediaType;
    @Schema(description = "태그 ID 목록으로 필터링. 쉼표(,)로 구분하여 여러 개를 전달할 수 있습니다.", example = "1,5")
    private List<Long> tagIds;

    public MemePostSearchCond(MediaType mediaType, List<Long> tagIds) {
        this.mediaType = mediaType;
        this.tagIds = tagIds;
    }
}
