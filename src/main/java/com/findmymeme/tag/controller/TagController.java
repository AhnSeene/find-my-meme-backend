package com.findmymeme.tag.controller;


import com.findmymeme.response.ApiResponse;
import com.findmymeme.response.ResponseUtil;
import com.findmymeme.response.SuccessCode;
import com.findmymeme.tag.dto.TagCreateRequest;
import com.findmymeme.tag.dto.TagCreateResponse;
import com.findmymeme.tag.dto.TagSummaryResponse;
import com.findmymeme.tag.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tags")
public class TagController {

    private final TagService tagService;

    @PostMapping
    public ResponseEntity<ApiResponse<TagCreateResponse>> createTag(
            @Valid @RequestBody TagCreateRequest request
            ) {
        return ResponseUtil.success(tagService.createTag(request), SuccessCode.TAG_CREATE);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TagSummaryResponse>>> getTagsWithSubTags() {
        return ResponseUtil.success(tagService.getTagsWithSubTags(), SuccessCode.TAG_LIST);
    }
}
