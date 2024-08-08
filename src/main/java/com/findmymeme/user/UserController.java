package com.findmymeme.user;

import com.findmymeme.response.ApiResponse;
import com.findmymeme.response.MessageCode;
import com.findmymeme.response.ResponseUtil;
import com.findmymeme.user.dto.SignupRequest;
import com.findmymeme.user.dto.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserResponse>> signup(@Valid @RequestBody SignupRequest signupRequest) {
        return ResponseUtil.success(userService.signup(signupRequest), HttpStatus.CREATED, MessageCode.USER_SIGNUP_SUCCESS);
    }
}
