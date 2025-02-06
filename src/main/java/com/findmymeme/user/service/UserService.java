package com.findmymeme.user.service;

import com.findmymeme.config.jwt.JwtProperties;
import com.findmymeme.config.jwt.JwtTokenProvider;
import com.findmymeme.config.jwt.TokenStatus;
import com.findmymeme.exception.ErrorCode;
import com.findmymeme.exception.FindMyMemeException;
import com.findmymeme.file.service.FileStorageService;
import com.findmymeme.token.domain.RefreshToken;
import com.findmymeme.token.repository.RefreshTokenRepository;
import com.findmymeme.user.domain.CustomUserDetails;
import com.findmymeme.user.domain.Role;
import com.findmymeme.user.dto.*;
import com.findmymeme.user.repository.UserRepository;
import com.findmymeme.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static com.findmymeme.config.jwt.TokenCategory.*;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;
    private final FileStorageService fileStorageService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;
    @Value("${default.profile-image-url}")
    private String defaultProfileImageUrl;

    public SignupResponse signup(SignupRequest signupRequest) {
        checkDuplicateUsername(signupRequest.getUsername());

        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());
        User user = SignupRequest.toEntity(signupRequest, Role.ROLE_USER, encodedPassword, defaultProfileImageUrl);
        return new SignupResponse(userRepository.save(user));
    }

    public SignupResponse adminSignup(SignupRequest signupRequest) {
        checkDuplicateUsername(signupRequest.getUsername());

        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());
        User user = SignupRequest.toEntity(signupRequest, Role.ROLE_ADMIN, encodedPassword, defaultProfileImageUrl);
        return new SignupResponse(userRepository.save(user));
    }

    public LoginDto login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Long userId = userDetails.getUserId();
        Role role = userDetails.getRole();
        TokenDto tokenDto = generateTokens(userId, role);
        saveRefreshToken(tokenDto.getRefreshToken(), userId, role);

        return LoginDto.builder()
                .accessToken(tokenDto.getAccessToken())
                .refreshToken(tokenDto.getRefreshToken())
                .username(userDetails.getUsername())
                .role(userDetails.getRole().name())
                .build();
    }

    public TokenDto reissueToken(String refreshToken) {
        validateRefreshToken(refreshToken);

        RefreshToken storedToken = refreshTokenRepository.findById(refreshToken)
                .orElseThrow(() -> new FindMyMemeException(ErrorCode.INVALID_TOKEN));

        Long userId = storedToken.getUserId();
        Role role = storedToken.getRole();

        TokenDto tokenDto = generateTokens(userId, role);
        refreshTokenRepository.deleteById(refreshToken);
        saveRefreshToken(tokenDto.getRefreshToken(), userId, role);
        return tokenDto;
    }

    private void validateRefreshToken(String refreshToken) {
        TokenStatus tokenStatus = jwtTokenProvider.validateToken(refreshToken);
        if (tokenStatus.isInvalid() || !jwtTokenProvider.getTokenCategory(refreshToken).isRefreshToken()) {
            throw new FindMyMemeException(ErrorCode.INVALID_TOKEN);
        }

        if (tokenStatus.isExpired()) {
            throw new FindMyMemeException(ErrorCode.EXPIRED_TOKEN);
        }
    }

    private TokenDto generateTokens(Long userId, Role role) {
        String accessToken = jwtTokenProvider.generateToken(
                userId,
                role.name(),
                jwtProperties.getAccessExpireTime(),
                ACCESS
        );

        String refreshToken = jwtTokenProvider.generateToken(
                userId,
                role.name(),
                jwtProperties.getRefreshExpireTime(),
                REFRESH
        );

        return new TokenDto(accessToken, refreshToken);
    }

    public void logout(String refreshToken) {
        TokenStatus tokenStatus = jwtTokenProvider.validateToken(refreshToken);
        if (tokenStatus.isInvalid() || !jwtTokenProvider.getTokenCategory(refreshToken).isRefreshToken()) {
            throw new FindMyMemeException(ErrorCode.INVALID_TOKEN);
        }

        if (tokenStatus.isExpired()) {
            return;
        }

        refreshTokenRepository.deleteById(refreshToken);
    }

    private void saveRefreshToken(String refreshToken, Long userId, Role role) {
        refreshTokenRepository.save(RefreshToken.builder()
                .refresh(refreshToken)
                .expiredAt(jwtTokenProvider.getExpireTime(refreshToken))
                .userId(userId)
                .role(role)
                .build());
    }

    public UserInfoResponse getMyInfo(Long userId) {
        User user = getUserById(userId);
        return new UserInfoResponse(user);
    }

    @Transactional
    public UserProfileImageResponse updateProfileImage(MultipartFile file, Long userId) {
        User user = getUserById(userId);
        String profileImageUrl = fileStorageService.storePermanentFile(file);
        user.updateProfileImageUrl(profileImageUrl);
        return new UserProfileImageResponse(profileImageUrl);
    }

    public boolean existsUsername(UsernameCheckRequest request) {
        return userRepository.existsByUsername(request.getUsername());
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new FindMyMemeException(ErrorCode.NOT_FOUND_USER));
    }


    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new FindMyMemeException(ErrorCode.NOT_FOUND_USER));
    }

    private void checkDuplicateUsername(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new FindMyMemeException(ErrorCode.ALREADY_EXIST_USERNAME);
        }
    }
}
