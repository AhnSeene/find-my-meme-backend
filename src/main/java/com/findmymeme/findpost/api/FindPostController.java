package com.findmymeme.findpost.api;

import com.findmymeme.common.resolver.CurrentUserId;
import com.findmymeme.findpost.domain.FindStatus;
import com.findmymeme.findpost.dto.*;
import com.findmymeme.findpost.service.FindPostReadService;
import com.findmymeme.findpost.service.FindPostWriteService;
import com.findmymeme.response.ApiResponse;
import com.findmymeme.response.MyPage;
import com.findmymeme.response.ResponseUtil;
import com.findmymeme.response.SuccessCode;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Tag(name = "4. Find Posts")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/find-posts")
public class FindPostController {

    private final FindPostWriteService findPostWriteService;
    private final FindPostReadService findPostReadService;

    @Operation(summary = "찾아줘 게시글 작성", description = "새로운 '찾아줘' 게시글을 작성합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "게시글 작성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "입력값 유효성 검사 실패", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @PostMapping
    public ResponseEntity<ApiResponse<FindPostUploadResponse>> uploadFindPost(
            @Valid @RequestBody FindPostUploadRequest request,
            @Parameter(hidden = true) @CurrentUserId Long userId
    ) {
        return ResponseUtil.success(findPostWriteService.uploadFindPost(request, userId),
                SuccessCode.FIND_POST_UPLOAD);
    }

    @Operation(summary = "찾아줘 게시글 상세 조회", description = "특정 게시글의 상세 내용을 댓글과 함께 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게시글 상세 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 게시글", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FindPostGetResponse>> getFindPost(
            @PathVariable("id") Long findPostId,
            @Parameter(hidden = true) @CurrentUserId(required = false) Optional<Long> userId
    ) {
        return ResponseUtil.success(findPostReadService.getFindPost(findPostId, userId),
                SuccessCode.FIND_POST_GET);
    }

    @Operation(summary = "찾아줘 게시글 목록 조회", description = "게시판의 모든 글 목록을 조회합니다. 상태(FIND/FOUND)로 필터링할 수 있습니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<MyPage<FindPostSummaryResponse>>> getFindPosts(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam(name = "page", defaultValue = "0") int page,
            @Parameter(description = "페이지 당 게시물 수", example = "5") @RequestParam(name = "size", defaultValue = "5") int size,
            @Parameter(description = "조회할 게시물 상태. **가능한 값: FIND, FOUND**", example = "FIND")
            @RequestParam(name = "status", required = false) FindStatus findStatus
    ) {
        MyPage<FindPostSummaryResponse> posts;
        if (findStatus != null) {
            posts = new MyPage<>(findPostReadService.getFindPostsByFindStatus(page, size, findStatus));
        } else {
            posts = new MyPage<>(findPostReadService.getFindPosts(page, size));
        }
        return ResponseUtil.success(posts, SuccessCode.FIND_POST_LIST);
    }

    @Operation(summary = "특정 사용자의 '찾아줘' 게시글 목록 조회", description = "특정 사용자가 작성한 '찾아줘' 게시글 목록을 조회합니다.")
    @GetMapping("/users/{authorName}")
    public ResponseEntity<ApiResponse<MyPage<MyFindPostSummaryResponse>>> getMyFindPosts(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam(name = "page", defaultValue = "0") int page,
            @Parameter(description = "페이지 당 게시물 수", example = "5") @RequestParam(name = "size", defaultValue = "5") int size,
            @Parameter(description = "조회할 게시물 상태. **가능한 값: FIND, FOUND**", example = "FIND")
            @RequestParam(name = "status", required = false) FindStatus findStatus,
            @PathVariable("authorName") String authorName
    ) {
        MyPage<MyFindPostSummaryResponse> posts;
        if (findStatus != null) {
            posts = new MyPage<>(findPostReadService.getFindPostsByAuthorAndFindStatus(page, size, authorName, findStatus));
        } else {
            posts = new MyPage<>(findPostReadService.getFindPostsByAuthor(page, size, authorName));
        }
        return ResponseUtil.success(posts, SuccessCode.FIND_POST_AUTHOR_LIST);
    }

    @Operation(summary = "찾아줘 게시글 수정", description = "본인이 작성한 '찾아줘' 게시글을 수정합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게시글 수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "게시글 수정 권한 없음", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 게시글", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FindPostUpdateResponse>> updateFindPost(
            @PathVariable("id") Long findPostId,
            @Valid @RequestBody FindPostUpdateRequest request,
            @Parameter(hidden = true) @CurrentUserId Long userId
    ) {
        return ResponseUtil.success(findPostWriteService.updateFindPost(request, findPostId, userId),
                SuccessCode.FIND_POST_UPDATE);
    }

    @Operation(summary = "댓글 채택", description = "게시물 작성자가 '밈'을 찾아준 댓글을 채택합니다. 게시물 상태가 'FOUND'로 변경됩니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "댓글 채택 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "댓글 채택 권한 없음", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 게시글 또는 댓글", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이미 채택된 게시글", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @PostMapping("/{postId}/comments/{commentId}/select")
    public ResponseEntity<ApiResponse<FindPostFoundResponse>> found(
            @PathVariable("postId") Long findPostId,
            @PathVariable("commentId") Long commentId,
            @Parameter(hidden = true) @CurrentUserId Long userId
    ) {
        return ResponseUtil.success(findPostWriteService.selectComment(findPostId, commentId, userId),
                SuccessCode.FIND_POST_FOUND);
    }

    @Operation(summary = "찾아줘 게시글 삭제", description = "본인이 작성한 '찾아줘' 게시글을 삭제합니다. (Soft Delete)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게시글 삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "게시글 삭제 권한 없음", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 게시글", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> softDelete(
            @PathVariable("postId") Long findPostId,
            @Parameter(hidden = true) @CurrentUserId Long userId
    ) {
        findPostWriteService.softDelete(findPostId, userId);
        return ResponseUtil.success(null,
                SuccessCode.FIND_POST_DELETE);
    }
}
