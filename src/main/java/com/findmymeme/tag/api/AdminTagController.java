package com.findmymeme.tag.api;

import com.findmymeme.response.ApiResponse;
import com.findmymeme.response.ResponseUtil;
import com.findmymeme.response.SuccessCode;
import com.findmymeme.tag.dto.TagCreateRequest;
import com.findmymeme.tag.dto.TagCreateResponse;
import com.findmymeme.tag.service.TagService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "8. Admin")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/tags")
public class AdminTagController {

    private final TagService tagService;

    @Operation(summary = "새 태그 생성", description = "새로운 태그를 시스템에 추가합니다. 관리자(ADMIN) 권한이 필요합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "태그 생성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 입력값 (e.g., 부모 태그 ID가 존재하지 않음)", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한이 없는 사용자 (관리자가 아님)", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이미 존재하는 태그 이름 또는 슬러그", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<TagCreateResponse>> createTag(
            @Valid @RequestBody TagCreateRequest request
    ) {
        return ResponseUtil.success(tagService.createTag(request), SuccessCode.TAG_CREATE);
    }
}
