package com.findmymeme.user.service;

import com.findmymeme.config.jwt.JwtTokenProvider;
import com.findmymeme.exception.ErrorCode;
import com.findmymeme.exception.FindMyMemeException;
import com.findmymeme.user.domain.CustomUserDetails;
import com.findmymeme.user.dto.LoginRequest;
import com.findmymeme.user.dto.LoginResponse;
import com.findmymeme.user.repository.UserRepository;
import com.findmymeme.user.domain.User;
import com.findmymeme.user.dto.SignupRequest;
import com.findmymeme.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public UserResponse signup(SignupRequest signupRequest) {
        checkDuplicateUsername(signupRequest.getUsername());

        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());
        User user = SignupRequest.toEntity(signupRequest, encodedPassword);
        return new UserResponse(userRepository.save(user));
    }

    public LoginResponse login(LoginRequest loginRequest) {
        User user = getUserByUsername(loginRequest.getUsername());
        validatePassword(loginRequest.getPassword(), user.getPassword());
        return new LoginResponse(jwtTokenProvider.generateToken(new CustomUserDetails(user)));
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
