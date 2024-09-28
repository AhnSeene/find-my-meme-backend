package com.findmymeme.user.service;

import com.findmymeme.config.jwt.JwtTokenProvider;
import com.findmymeme.exception.ErrorCode;
import com.findmymeme.exception.FindMyMemeException;
import com.findmymeme.file.service.FileStorageService;
import com.findmymeme.user.domain.CustomUserDetails;
import com.findmymeme.user.domain.Role;
import com.findmymeme.user.dto.*;
import com.findmymeme.user.repository.UserRepository;
import com.findmymeme.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final FileStorageService fileStorageService;
    @Value("${default.profile-image-url}")
    private String defaultProfileImageUrl;

    @Transactional
    public SignupResponse signup(SignupRequest signupRequest) {
        checkDuplicateUsername(signupRequest.getUsername());

        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());
        User user = SignupRequest.toEntity(signupRequest, encodedPassword, defaultProfileImageUrl);
        return new SignupResponse(userRepository.save(user));
    }

    @Transactional
    public SignupResponse adminSignup(SignupRequest signupRequest) {
        checkDuplicateUsername(signupRequest.getUsername());

        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());
        User user = SignupRequest.toEntity(signupRequest, Role.ROLE_ADMIN, encodedPassword, defaultProfileImageUrl);
        return new SignupResponse(userRepository.save(user));
    }

    public LoginResponse login(LoginRequest loginRequest) {
        User user = getUserByUsername(loginRequest.getUsername());
        validatePassword(loginRequest.getPassword(), user.getPassword());
        return LoginResponse.builder()
                .accessToken(jwtTokenProvider.generateToken(new CustomUserDetails(user), user.getId()))
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();
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

    private void validatePassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new FindMyMemeException(ErrorCode.INVALID_CREDENTIALS);
        }
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
