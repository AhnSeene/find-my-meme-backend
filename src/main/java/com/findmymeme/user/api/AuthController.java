package com.findmymeme.user.api;

import com.findmymeme.config.jwt.JwtProperties;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController {

    private static final String REFRESH = "refresh";
    private static final String ACCESS = "access";
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

        LoginResponse loginResponse = userService.login(request);
        Cookie refreshCookie = createRefreshCookie(loginResponse.getRefreshToken());
        response.addCookie(refreshCookie);
        response.addHeader(ACCESS, loginResponse.getAccessToken());
        return ResponseUtil.success(userService.login(request), SuccessCode.LOGIN);
    }

    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<ReissueTokenResponse>> reissueToken(
            @CookieValue(value = "refresh") String refreshToken, HttpServletResponse response
    ) {
        ReissueTokenResponse reissueTokenResponse = userService.reissueToken(refreshToken);
        Cookie refreshCookie = createRefreshCookie(reissueTokenResponse.getRefreshToken());
        response.addCookie(refreshCookie);
        response.addHeader(ACCESS, reissueTokenResponse.getAccessToken());
        return ResponseUtil.success(reissueTokenResponse, SuccessCode.REISSUE);
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
