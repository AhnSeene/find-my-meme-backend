package com.findmymeme.tag.controller;

import com.findmymeme.response.ApiResponse;
import com.findmymeme.response.ResponseUtil;
import com.findmymeme.response.SuccessCode;
import com.findmymeme.tag.dto.TagCreateRequest;
import com.findmymeme.tag.dto.TagCreateResponse;
import com.findmymeme.tag.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/tags")
public class AdminTagController {

    private final TagService tagService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<TagCreateResponse>> createTag(
            @Valid @RequestBody TagCreateRequest request
    ) {
        return ResponseUtil.success(tagService.createTag(request), SuccessCode.TAG_CREATE);
    }
}
