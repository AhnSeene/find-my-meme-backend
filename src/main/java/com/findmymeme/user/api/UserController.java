package com.findmymeme.user.api;

import com.findmymeme.exception.ErrorCode;
import com.findmymeme.response.ApiResponse;
import com.findmymeme.response.ResponseUtil;
import com.findmymeme.response.SuccessCode;
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
            Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());
        return ResponseUtil.success(userService.getMyInfo(userId),
                SuccessCode.USER_INFO
        );
    }

    @PostMapping("/profile-image")
    public ResponseEntity<ApiResponse<UserProfileImageResponse>> updateProfileImage(
            MultipartFile file,
             Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());
        return ResponseUtil.success(userService.updateProfileImage(file, userId),
                SuccessCode.USER_PROFILE_IMAGE_UPDATE
        );
    }

    @PostMapping("/check-username")
    public ResponseEntity<ApiResponse<Void>> checkUsername(@Valid @RequestBody UsernameCheckRequest request) {
        if (userService.existsUsername(request)) {
            return ResponseUtil.success(null, SuccessCode.USER_DUPLICATE_VALIDATION);
        }
        return ResponseUtil.error(null, ErrorCode.ALREADY_EXIST_USERNAME);
    }
}
