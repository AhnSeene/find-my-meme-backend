package com.findmymeme.user.api;

import com.findmymeme.config.jwt.JwtProperties;
import com.findmymeme.exception.ErrorCode;
import com.findmymeme.response.ApiResponse;
import com.findmymeme.response.SuccessCode;
import com.findmymeme.response.ResponseUtil;
import com.findmymeme.user.dto.*;
import com.findmymeme.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController {

    private static final String REFRESH = "refresh";

    private final UserService userService;
    private final JwtProperties jwtProperties;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(@Valid @RequestBody SignupRequest signupRequest) {
        return ResponseUtil.success(userService.signup(signupRequest), SuccessCode.SIGNUP);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request, HttpServletResponse response
    ) {

        try {
            LoginDto loginDto = userService.login(request);
            Cookie refreshCookie = createRefreshCookie(loginDto.getRefreshToken());
            response.addCookie(refreshCookie);
            return ResponseUtil.success(
                    LoginResponse.fromLoginDto(loginDto), SuccessCode.LOGIN
            );
        } catch (AuthenticationException e) {
            return ResponseUtil.error(null, ErrorCode.INVALID_CREDENTIALS);
        }

    }

    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<ReissueResponse>> reissueToken(
            @CookieValue(value = "refresh") String refreshToken, HttpServletResponse response
    ) {
        TokenDto tokenDto = userService.reissueToken(refreshToken);
        Cookie refreshCookie = createRefreshCookie(tokenDto.getRefreshToken());
        response.addCookie(refreshCookie);
        return ResponseUtil.success(new ReissueResponse(tokenDto.getAccessToken()), SuccessCode.REISSUE);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @CookieValue(value = "refresh") String refreshToken, HttpServletResponse response
    ) {
        userService.logout(refreshToken);
        Cookie cookie = new Cookie(REFRESH, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return ResponseUtil.success(null, SuccessCode.LOGOUT);
    }

    private Cookie createRefreshCookie(String refreshToken) {
        Cookie refreshCookie = new Cookie(REFRESH, refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(Math.toIntExact(jwtProperties.getRefreshExpireTime()));
        return refreshCookie;
    }

}
