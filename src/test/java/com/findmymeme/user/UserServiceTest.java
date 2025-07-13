package com.findmymeme.user;

import com.findmymeme.config.jwt.JwtProperties;
import com.findmymeme.config.jwt.JwtTokenProvider;
import com.findmymeme.exception.ErrorCode;
import com.findmymeme.exception.FindMyMemeException;
import com.findmymeme.file.service.FileStorageService;
import com.findmymeme.token.repository.RefreshTokenRepository;
import com.findmymeme.user.domain.Role;
import com.findmymeme.user.domain.User;
import com.findmymeme.user.dto.LoginRequest;
import com.findmymeme.user.dto.SignupRequest;
import com.findmymeme.user.dto.SignupResponse;
import com.findmymeme.user.repository.UserRepository;
import com.findmymeme.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private JwtProperties jwtProperties;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FileStorageService fileStorageService;

    @Test
    void 회원가입_성공() {
        // given
        String username = "newUser";
        String password = "password123!";
        String email = "newuser@example.com";
        SignupRequest signupRequest = SignupRequest.builder()
                .username(username)
                .password(password)
                .email(email)
                .build();
        User user = User.builder()
                .username(username)
                .password(password)
                .email(email)
                .profileImageUrl("default.jpg")
                .build();

        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn(password);
        when(userRepository.save(any(User.class))).thenReturn(user); // save가 user 반환하도록 설정

        // when
        SignupResponse response = userService.signup(signupRequest);

        // then
        assertThat(response.getUsername()).isEqualTo(username);
        assertThat(response.getEmail()).isEqualTo(email);
        verify(userRepository, times(1)).existsByUsername(username);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void 중복된_username으로_회원가입_실패_에러발생() {
        // Given
        String username = "newUser";
        String email = "newUser";
        String rawPassword = "password";

        SignupRequest signupRequest = SignupRequest.builder()
                .username(username)
                .password(rawPassword)
                .email(email)
                .build();

        when(userRepository.existsByUsername(username)).thenReturn(true); // existsByUsername만 사용

        // When & Then
        assertThatThrownBy(() -> userService.signup(signupRequest))
                .isInstanceOf(FindMyMemeException.class)
                .hasMessage(ErrorCode.CONFLICT_USERNAME_EXISTS.getMessage());
        verify(userRepository, times(1)).existsByUsername(username);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void 중복된_email로_회원가입_실패_에러발생() {
        // Given
        String username = "newUser2";
        String email = "duplicate@example.com";
        String rawPassword = "password123!";
        SignupRequest signupRequest = SignupRequest.builder()
                .username(username)
                .password(rawPassword)
                .email(email)
                .build();

        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(userRepository.existsByEmail(email)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.signup(signupRequest))
                .isInstanceOf(FindMyMemeException.class)
                .hasMessage(ErrorCode.CONFLICT_EMAIL_EXISTS.getMessage());
        verify(userRepository, times(1)).existsByUsername(username);
        verify(userRepository, times(1)).existsByEmail(email);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void 존재하지_않는_아이디일경우_예외_발생() {
        // given
        LoginRequest loginRequest = new LoginRequest("invalid_user", "password");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new UsernameNotFoundException("존재하지 않는 사용자"));

        // when, then
        assertThrows(UsernameNotFoundException.class, () -> userService.login(loginRequest));
    }

    @Test
    void 비밀번호가_틀릴_경우_예외발생() {
        // given
        LoginRequest loginRequest = new LoginRequest("valid_user", "wrong_password");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("비밀번호 오류"));

        // when, then
        assertThrows(BadCredentialsException.class, () -> userService.login(loginRequest));
    }

    @Test
    void 프로필_이미지_변경_성공() {
        // given
        Long userId = 1L;
        User user = User.builder()
                .username("testuser")
                .password("password123!")
                .email("testuser@example.com")
                .profileImageUrl("old.jpg")
                .build();
        MultipartFile file = mock(MultipartFile.class);
        String newImageUrl = "new-profile.jpg";

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(fileStorageService.storePermanentFile(file, com.findmymeme.file.domain.FileType.PROFILE)).thenReturn(newImageUrl);

        // when
        var response = userService.updateProfileImage(file, userId);

        // then
        assertThat(response.getProfileImageUrl()).isEqualTo(newImageUrl);
        assertThat(user.getProfileImageUrl()).isEqualTo(newImageUrl);
        verify(userRepository, times(1)).findById(userId);
        verify(fileStorageService, times(1)).storePermanentFile(file, com.findmymeme.file.domain.FileType.PROFILE);
    }

    @Test
    void 내_정보_조회_성공() {
        // given
        Long userId = 2L;
        User user = User.builder()
                .username("myuser")
                .password("password123!")
                .email("myuser@example.com")
                .profileImageUrl("profile.jpg")
                .role(Role.ROLE_USER)
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // when
        var response = userService.getMyInfo(userId);

        // then
        assertThat(response.getUsername()).isEqualTo(user.getUsername());
        assertThat(response.getEmail()).isEqualTo(user.getEmail());
        assertThat(response.getProfileImageUrl()).isEqualTo(user.getProfileImageUrl());
        verify(userRepository, times(1)).findById(userId);
    }

}