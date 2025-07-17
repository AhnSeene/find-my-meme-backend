package com.findmymeme.user.api;

import com.findmymeme.common.resolver.CurrentUserId;
import com.findmymeme.exception.ErrorCode;
import com.findmymeme.response.ApiResponse;
import com.findmymeme.response.ResponseUtil;
import com.findmymeme.response.SuccessCode;
import com.findmymeme.user.dto.EmailCheckRequest;
import com.findmymeme.user.dto.UserInfoResponse;
import com.findmymeme.user.dto.UserProfileImageResponse;
import com.findmymeme.user.dto.UsernameCheckRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.findmymeme.user.service.UserService;


@Tag(name = "2. Users")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Value("${file.base-url}")
    private String fileBaseUrl;

    @Operation(summary = "내 정보 조회", description = "인증된 사용자의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "내 정보 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserInfoResponse>> getMyInfo(
            @Parameter(hidden = true) @CurrentUserId Long userId
    ) {
        return ResponseUtil.success(userService.getMyInfo(userId),
                SuccessCode.USER_INFO
        );
    }

    @Operation(summary = "프로필 이미지 변경", description = "사용자의 프로필 이미지를 변경합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "프로필 이미지 변경 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 파일 요청 (e.g., 파일 비어있음)", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @PostMapping("/profile-image")
    public ResponseEntity<ApiResponse<UserProfileImageResponse>> updateProfileImage(
            @Parameter(description = "업로드할 이미지 파일") MultipartFile file,
            @Parameter(hidden = true) @CurrentUserId Long userId
    ) {
        return ResponseUtil.success(userService.updateProfileImage(file, userId),
                SuccessCode.USER_PROFILE_IMAGE_UPDATE
        );
    }

    @Operation(summary = "아이디 중복 확인", description = "회원가입 시 사용할 아이디의 중복 여부를 확인합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "사용 가능한 아이디"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "입력값 유효성 검사 실패", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이미 사용 중인 아이디", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @PostMapping("/check-username")
    public ResponseEntity<ApiResponse<Void>> checkUsername(@Valid @RequestBody UsernameCheckRequest request) {
        if (userService.existsUsername(request)) {
            return ResponseUtil.error(null, ErrorCode.CONFLICT_USERNAME_EXISTS);
        }
        return ResponseUtil.success(null, SuccessCode.USER_DUPLICATE_VALIDATION);
    }

    @Operation(summary = "이메일 중복 확인", description = "회원가입 시 사용할 이메일의 중복 여부를 확인합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "사용 가능한 이메일"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "입력값 유효성 검사 실패", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이미 사용 중인 이메일", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @PostMapping("/check-email")
    public ResponseEntity<ApiResponse<Void>> checkEmail(@Valid @RequestBody EmailCheckRequest request) {
        if (userService.existsEmail(request)) {
            return ResponseUtil.error(null, ErrorCode.CONFLICT_EMAIL_EXISTS);
        }
        return ResponseUtil.success(null, SuccessCode.EMAIL_DUPLICATE_VALIDATION);
    }
}
