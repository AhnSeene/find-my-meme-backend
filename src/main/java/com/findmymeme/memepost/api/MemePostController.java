package com.findmymeme.memepost.api;

import com.findmymeme.response.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.findmymeme.common.resolver.CurrentUserId;
import com.findmymeme.memepost.domain.MemePostSort;
import com.findmymeme.memepost.dto.*;
import com.findmymeme.memepost.service.MemePostLikeService;
import com.findmymeme.memepost.service.MemePostService;
import com.findmymeme.response.MySlice;
import com.findmymeme.response.ResponseUtil;
import com.findmymeme.response.SuccessCode;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name = "3. Meme Posts")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/meme-posts")
public class MemePostController {

    private static final String ATTACHMENT_FILENAME_TEMPLATE = "attachment; filename=\"%s\"";
    private final MemePostService memePostService;
    private final MemePostLikeService memePostLikeService;

    @Operation(summary = "밈 게시물 업로드", description = "사용자가 새로운 밈 게시물을 업로드합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "게시물 업로드 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 유효성 검사 실패", content = @Content(schema = @Schema(implementation = ApiResult.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content(schema = @Schema(implementation = ApiResult.class))),
            @ApiResponse(responseCode = "404", description = "임시 파일 정보를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ApiResult.class)))
    })
    @PostMapping
    public ResponseEntity<ApiResult<MemePostUploadResponse>> uploadMemePost(
            @Valid @RequestBody MemePostUploadRequest request,
            @Parameter(hidden = true) @CurrentUserId(required = false) Long userId
    ) {
        return ResponseUtil.success(memePostService.uploadMemePost(request, userId),
                SuccessCode.MEME_POST_UPLOAD
        );
    }

    @Operation(summary = "밈 게시물 단건 조회", description = "밈 게시물 ID로 특정 게시물의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시물 상세 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 밈 게시물", content = @Content(schema = @Schema(implementation = ApiResult.class)))
    })
    @GetMapping("/{memePostId}")
    public ResponseEntity<ApiResult<MemePostGetResponse>> getMemePost(
            @PathVariable("memePostId") Long memePostId,
            @Parameter(hidden = true) @CurrentUserId(required = false) Optional<Long> userId
    ) {
        return ResponseUtil.success(memePostService.getMemePost(memePostId, userId),
                SuccessCode.MEME_POST_GET
        );
    }

    @Operation(summary = "밈 게시물 단건 조회 (Redis)", description = "Redis를 활용하여 밈 게시물 상세 정보를 조회합니다.")
    @GetMapping("/{memePostId}/redis")
    public ResponseEntity<ApiResult<MemePostGetResponse>> getMemePostRedis(
            @PathVariable("memePostId") Long memePostId,
            @Parameter(hidden = true) @CurrentUserId(required = false) Optional<Long> userId
    ) {
        return ResponseUtil.success(memePostService.getMemePostRedis(memePostId, userId),
                SuccessCode.MEME_POST_GET
        );
    }

    @Operation(summary = "밈 게시물 다운로드", description = "Presigned URL을 통해 밈 게시물 이미지를 다운로드합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "다운로드 URL로 리다이렉트"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 밈 게시물", content = @Content(schema = @Schema(implementation = ApiResult.class)))
    })
    @GetMapping("/{memePostId}/download")
    public ResponseEntity<?> downloadMemePost(
            @PathVariable("memePostId") Long memePostId
    ) {
        MemePostDownloadDto downloadDto = memePostService.download(memePostId);
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, downloadDto.getPresignedUrl())
                .build();
    }

    @Operation(summary = "밈 게시물 목록 검색", description = "다양한 조건(태그, 미디어 타입 등)으로 밈 게시물을 검색합니다.")
    @GetMapping()
    public ResponseEntity<ApiResult<MySlice<MemePostSummaryResponse>>> searchMemePosts(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 당 게시물 수", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "검색 조건") @ModelAttribute MemePostSearchCond searchCond,
            @Parameter(hidden = true) @CurrentUserId(required = false) Optional<Long> userId
    ) {
        Slice<MemePostSummaryResponse> responses = memePostService.getMemePostsWithLikeInfo(
                page, size, MemePostSort.CREATED, searchCond, userId
        );
        return ResponseUtil.success(new MySlice<>(responses), SuccessCode.MEME_POST_LIST);
    }

    @Operation(summary = "연관 밈 게시물 추천", description = "특정 게시물과 연관된 태그를 기반으로 다른 게시물을 추천합니다.")
    @GetMapping("/{memePostId}/recommendations")
    public ResponseEntity<ApiResult<List<MemePostSummaryResponse>>> getRecommendations(
            @PathVariable("memePostId") Long memePostId,
            @Parameter(description = "추천 받을 게시물 수", example = "20") @RequestParam(defaultValue = "20") int size,
            @Parameter(hidden = true) @CurrentUserId(required = false) Optional<Long> userId
    ) {
        List<MemePostSummaryResponse> responses = memePostService.getRecommendedPostsWithLikeInfo(memePostId, size, userId);
        return ResponseUtil.success(responses, SuccessCode.MEME_POST_LIST);
    }

    @Operation(summary = "밈 게시물 좋아요 토글", description = "사용자가 특정 밈 게시물에 '좋아요'를 누르거나 취소합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "좋아요/취소 처리 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content(schema = @Schema(implementation = ApiResult.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 밈 게시물", content = @Content(schema = @Schema(implementation = ApiResult.class)))
    })
    @PostMapping("/{memePostId}/toggleLike")
    public ResponseEntity<ApiResult<MemePostLikeResponse>> toggleLikeMemePost(
            @PathVariable("memePostId") Long memePostId,
            @Parameter(hidden = true) @CurrentUserId Long userId
    ) {
        return ResponseUtil.success(memePostLikeService.toggleLike(memePostId, userId),
                SuccessCode.MEME_POST_LIKE
        );
    }

    @Operation(summary = "밈 게시물 삭제", description = "게시물 소유자가 본인의 게시물을 삭제합니다. (Soft Delete)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시물 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content(schema = @Schema(implementation = ApiResult.class))),
            @ApiResponse(responseCode = "403", description = "게시물 삭제 권한 없음", content = @Content(schema = @Schema(implementation = ApiResult.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 밈 게시물", content = @Content(schema = @Schema(implementation = ApiResult.class)))
    })
    @DeleteMapping("/{memePostId}")
    public ResponseEntity<ApiResult<Void>> softDelete(
            @PathVariable("memePostId") Long memePostId,
            @Parameter(hidden = true) @CurrentUserId Long userId
    ) {
        memePostService.softDelete(memePostId, userId);
        return ResponseUtil.success(
                null,
                SuccessCode.MEME_POST_DELETE
        );
    }

    @Operation(summary = "특정 사용자 게시물 목록 조회", description = "사용자 이름(username)으로 해당 사용자가 올린 게시물 목록을 조회합니다.")
    @GetMapping("/users/{authorName}")
    public ResponseEntity<ApiResult<MemePostUserSummaryResponse>> getMemePostsByAuthorName(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 당 게시물 수", example = "10") @RequestParam(defaultValue = "10") int size,
            @PathVariable("authorName") String authorName,
            @Parameter(hidden = true) @CurrentUserId(required = false) Optional<Long> userId
    ) {
        MemePostUserSummaryResponse responses = memePostService.getMemePostsByAuthorNameWithLikeInfo(page, size, authorName, userId);
        return ResponseUtil.success(responses, SuccessCode.MEME_POST_AUTHOR_LIST);
    }

    @Operation(summary = "내 밈 게시물 목록 조회", description = "인증된 사용자가 본인이 업로드한 모든 밈 게시물 목록을 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<ApiResult<MemePostUserSummaryResponse>> getMyMemePosts(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 당 게시물 수", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(hidden = true) @CurrentUserId Long userId
    ) {
        MemePostUserSummaryResponse responses = memePostService.getMyMemePosts(page, size, userId);
        return ResponseUtil.success(responses, SuccessCode.MEME_POST_MY_LIST);
    }

    @Operation(summary = "밈 게시물 랭킹 조회 (전체 기간)", description = "전체 기간 동안 '좋아요' 또는 '조회수'가 높은 순서로 랭킹을 조회합니다.")
    @GetMapping("/ranks/all")
    public ResponseEntity<ApiResult<List<MemePostSummaryResponse>>> getRankedPostsAllPeriod(
            @RequestParam(name = "sort", defaultValue = "LIKE") Sort sort,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        List<MemePostSummaryResponse> responses = memePostService.getRankedPostsAllPeriod(page, size, sort);
        return ResponseUtil.success(responses, SuccessCode.MEME_POST_LIST);
    }

    @Operation(summary = "밈 게시물 랭킹 조회 (기간별)", description = "주간/월간 등 특정 기간 동안 '좋아요'가 높은 순서로 랭킹을 조회합니다.")
    @GetMapping("/ranks/period")
    public ResponseEntity<ApiResult<List<MemePostSummaryResponse>>> getRankedPostsWithPeriod(
            @RequestParam(name = "period") Period period,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        List<MemePostSummaryResponse> responses = memePostService.getRankedPostsWithPeriod(page, size, period);
        return ResponseUtil.success(responses, SuccessCode.MEME_POST_LIST);
    }

}
