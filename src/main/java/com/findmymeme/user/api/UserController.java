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
import com.findmymeme.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {


    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserInfoResponse>> getMyInfo(
            @CurrentUserId(required = false) Long userId
    ) {
        return ResponseUtil.success(userService.getMyInfo(userId),
                SuccessCode.USER_INFO
        );
    }

    @PostMapping("/profile-image")
    public ResponseEntity<ApiResponse<UserProfileImageResponse>> updateProfileImage(
            MultipartFile file,
            @CurrentUserId(required = false) Long userId
    ) {
        return ResponseUtil.success(userService.updateProfileImage(file, userId),
                SuccessCode.USER_PROFILE_IMAGE_UPDATE
        );
    }

    @PostMapping("/check-username")
    public ResponseEntity<ApiResponse<Void>> checkUsername(@Valid @RequestBody UsernameCheckRequest request) {
        if (userService.existsUsername(request)) {
            return ResponseUtil.error(null, ErrorCode.ALREADY_EXIST_USERNAME);
        }
        return ResponseUtil.success(null, SuccessCode.USER_DUPLICATE_VALIDATION);
    }

    @PostMapping("/check-email")
    public ResponseEntity<ApiResponse<Void>> checkUsername(@Valid @RequestBody EmailCheckRequest request) {
        if (userService.existsEmail(request)) {
            return ResponseUtil.error(null, ErrorCode.ALREADY_EXIST_USERNAME);
        }
        return ResponseUtil.success(null, SuccessCode.EMAIL_DUPLICATE_VALIDATION);
    }
}
