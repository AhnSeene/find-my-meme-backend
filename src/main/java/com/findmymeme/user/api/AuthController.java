package com.findmymeme.user.api;

import com.findmymeme.config.jwt.JwtProperties;
import com.findmymeme.exception.ErrorCode;
import com.findmymeme.response.ApiResponse;
import com.findmymeme.response.SuccessCode;
import com.findmymeme.response.ResponseUtil;
import com.findmymeme.user.dto.*;
import com.findmymeme.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@Tag(name = "1. Authentication")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController {

    private static final String REFRESH = "refresh";

    private final UserService userService;
    private final JwtProperties jwtProperties;

    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "회원가입 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "입력값 유효성 검사 실패", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "아이디 또는 이메일 중복", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(@Valid @RequestBody SignupRequest signupRequest) {
        return ResponseUtil.success(userService.signup(signupRequest), SuccessCode.SIGNUP);
    }

    @Operation(summary = "로그인", description = "아이디와 비밀번호로 로그인하고 Access Token을 발급받습니다. Refresh Token은 HttpOnly 쿠키로 설정됩니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그인 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "아이디 또는 비밀번호 틀림", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request, @Parameter(hidden = true) HttpServletResponse response
    ) {

        try {
            LoginDto loginDto = userService.login(request);
            Cookie refreshCookie = createRefreshCookie(loginDto.getRefreshToken());
            response.addCookie(refreshCookie);
            return ResponseUtil.success(
                    LoginResponse.fromLoginDto(loginDto), SuccessCode.LOGIN
            );
        } catch (AuthenticationException e) {
            return ResponseUtil.error(null, ErrorCode.AUTH_INVALID_CREDENTIALS);
        }

    }

    @Operation(summary = "토큰 재발급", description = "HttpOnly 쿠키로 전달된 Refresh Token을 사용하여 새로운 Access Token을 재발급받습니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "토큰 재발급 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "유효하지 않거나 만료된 리프레시 토큰", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<ReissueResponse>> reissueToken(
            @Parameter(hidden = true) @CookieValue(value = "refresh") String refreshToken, @Parameter(hidden = true) HttpServletResponse response
    ) {
        TokenDto tokenDto = userService.reissueToken(refreshToken);
        Cookie refreshCookie = createRefreshCookie(tokenDto.getRefreshToken());
        response.addCookie(refreshCookie);
        return ResponseUtil.success(new ReissueResponse(tokenDto.getAccessToken()), SuccessCode.REISSUE);
    }

    @Operation(summary = "로그아웃", description = "서버에 저장된 Refresh Token을 삭제하고, 클라이언트의 Refresh Token 쿠키를 만료시킵니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그아웃 성공")
    })
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @Parameter(hidden = true) @CookieValue(value = "refresh") String refreshToken, @Parameter(hidden = true) HttpServletResponse response
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
