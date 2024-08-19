package com.findmymeme.user.api;

import com.findmymeme.response.ApiResponse;
import com.findmymeme.response.SuccessCode;
import com.findmymeme.response.ResponseUtil;
import com.findmymeme.user.dto.LoginRequest;
import com.findmymeme.user.dto.LoginResponse;
import com.findmymeme.user.dto.SignupRequest;
import com.findmymeme.user.dto.UserResponse;
import com.findmymeme.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserResponse>> signup(@Valid @RequestBody SignupRequest signupRequest) {
        return ResponseUtil.success(userService.signup(signupRequest), SuccessCode.SIGNUP);
    }

    @PostMapping("login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {
        return ResponseUtil.success(userService.login(request), SuccessCode.LOGIN);
    }
}
