package com.findmymeme.tag.api;


import com.findmymeme.response.ApiResult;
import com.findmymeme.response.ResponseUtil;
import com.findmymeme.response.SuccessCode;
import com.findmymeme.tag.dto.TagSummaryResponse;
import com.findmymeme.tag.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "6. Tags")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tags")
public class TagController {

    private final TagService tagService;

    @Operation(summary = "전체 태그 목록 조회", description = "검색에 사용할 수 있는 모든 태그의 목록을 계층 구조로 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "태그 목록 조회 성공")
    })
    @GetMapping
    public ResponseEntity<ApiResult<List<TagSummaryResponse>>> getTagsWithSubTags() {
        return ResponseUtil.success(tagService.getTagsWithSubTags(), SuccessCode.TAG_LIST);
    }
}
