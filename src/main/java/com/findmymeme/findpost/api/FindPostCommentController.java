package com.findmymeme.findpost.api;

import com.findmymeme.common.resolver.CurrentUserId;
import com.findmymeme.findpost.dto.*;
import com.findmymeme.findpost.service.FindPostCommentReadService;
import com.findmymeme.findpost.service.FindPostCommentWriteService;
import com.findmymeme.response.ApiResult;
import com.findmymeme.response.ResponseUtil;
import com.findmymeme.response.SuccessCode;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name = "5. Find Post Comments")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/find-posts/{postId}/comments")
public class FindPostCommentController {

    private final FindPostCommentWriteService commentWriteService;
    private final FindPostCommentReadService commentReadService;

    @Operation(summary = "댓글 작성", description = "특정 게시글에 새로운 댓글을 작성합니다. (대댓글의 경우 parentCommentId 포함)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "댓글 작성 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 유효성 검사 실패 또는 삭제된 게시물/댓글에 작성 시도", content = @Content(schema = @Schema(implementation = ApiResult.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content(schema = @Schema(implementation = ApiResult.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 게시글 또는 부모 댓글", content = @Content(schema = @Schema(implementation = ApiResult.class)))
    })
    @PostMapping
    public ResponseEntity<ApiResult<FindPostCommentAddResponse>> addComment(
            @PathVariable("postId") Long postId,
            @Valid @RequestBody FindPostCommentAddRequest request,
            @Parameter(hidden = true) @CurrentUserId Long userId
    ) {
        return ResponseUtil.success(
                commentWriteService.addComment(request, postId, userId),
                SuccessCode.FIND_POST_COMMENT_UPLOAD
        );
    }

    @Operation(summary = "댓글 단건 조회", description = "특정 댓글의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 조회 성공"),
            @ApiResponse(responseCode = "400", description = "댓글이 해당 게시글에 속하지 않음", content = @Content(schema = @Schema(implementation = ApiResult.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 게시글 또는 댓글", content = @Content(schema = @Schema(implementation = ApiResult.class)))
    })
    @GetMapping("/{commentId}")
    public ResponseEntity<ApiResult<FindPostCommentGetResponse>> getComment(
            @PathVariable("postId") Long postId,
            @PathVariable("commentId") Long commentId,
            @Parameter(hidden = true) @CurrentUserId(required = false) Optional<Long> userId
    ) {
        return ResponseUtil.success(
                commentReadService.getComment(postId, commentId, userId),
                SuccessCode.FIND_POST_COMMENT_GET
        );
    }

    @Operation(summary = "게시글의 댓글 목록 조회", description = "특정 게시글에 달린 모든 댓글과 대댓글을 계층 구조로 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResult<List<FindPostCommentSummaryResponse>>> getComments(
            @PathVariable("postId") Long postId
    ) {
        return ResponseUtil.success(
                commentReadService.getCommentsWithReplys(postId),
                SuccessCode.FIND_POST_COMMENT_LIST
        );
    }

    @Operation(summary = "댓글 수정", description = "본인이 작성한 댓글의 내용을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 수정 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content(schema = @Schema(implementation = ApiResult.class))),
            @ApiResponse(responseCode = "403", description = "댓글 수정 권한 없음", content = @Content(schema = @Schema(implementation = ApiResult.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 게시글 또는 댓글", content = @Content(schema = @Schema(implementation = ApiResult.class))),
            @ApiResponse(responseCode = "409", description = "이미 삭제된 댓글은 수정 불가", content = @Content(schema = @Schema(implementation = ApiResult.class)))
    })
    @PutMapping("/{commentId}")
    public ResponseEntity<ApiResult<FindPostCommentUpdateResponse>> updateComment(
            @PathVariable("postId") Long postId,
            @PathVariable("commentId") Long commentId,
            @Valid @RequestBody FindPostCommentUpdateRequest request,
            @Parameter(hidden = true) @CurrentUserId Long userId
    ) {
        return ResponseUtil.success(
                commentWriteService.updateComment(request, postId, commentId, userId),
                SuccessCode.FIND_POST_COMMENT_UPDATE
        );
    }

    @Operation(summary = "댓글 삭제", description = "본인이 작성한 댓글을 삭제합니다. (Soft Delete)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content(schema = @Schema(implementation = ApiResult.class))),
            @ApiResponse(responseCode = "403", description = "댓글 삭제 권한 없음", content = @Content(schema = @Schema(implementation = ApiResult.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 게시글 또는 댓글", content = @Content(schema = @Schema(implementation = ApiResult.class))),
            @ApiResponse(responseCode = "409", description = "이미 삭제된 댓글", content = @Content(schema = @Schema(implementation = ApiResult.class)))
    })
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResult<FindPostCommentDeleteResponse>> softDelete(
            @PathVariable("postId") Long postId,
            @PathVariable("commentId") Long commentId,
            @Parameter(hidden = true) @CurrentUserId Long userId
    ) {
        return ResponseUtil.success(
                commentWriteService.softDelete(postId, commentId, userId),
                SuccessCode.FIND_POST_COMMENT_DELETE
        );
    }
}
