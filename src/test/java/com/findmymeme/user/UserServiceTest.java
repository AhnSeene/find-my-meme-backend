package com.findmymeme.user;

import com.findmymeme.exception.ErrorCode;
import com.findmymeme.exception.FindMyMemeException;
import com.findmymeme.user.domain.User;
import com.findmymeme.user.dto.SignupRequest;
import com.findmymeme.user.dto.UserResponse;
import com.findmymeme.user.repository.UserRepository;
import com.findmymeme.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    UserService userService;

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Test
    void 회원가입_성공() {
        // given
        String username = "newUser";
        String email = "newUser";
        String rawPassword = "password";
        String encodedPassword = "encodedPassword";

        SignupRequest signupRequest = SignupRequest.builder()
                .username(username)
                .password(rawPassword)
                .email(email)
                .build();

        User savedUser = User.builder()
                .username(username)
                .password(encodedPassword)
                .email(email)
                .build();

        when(userRepository.findByUsername(signupRequest.getUsername())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        //when
        UserResponse userResponse = userService.signup(signupRequest);

        //then
        assertThat(userResponse).isNotNull();
        assertThat(userResponse.getUsername()).isEqualTo(signupRequest.getUsername());
        assertThat(userResponse.getEmail()).isEqualTo(signupRequest.getEmail());

        verify(userRepository, times(1)).findByUsername(username);
        verify(passwordEncoder, times(1)).encode(rawPassword);
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

        when(userRepository.findByUsername(signupRequest.getUsername())).thenReturn(Optional.of(new User()));

        // When & Then
        assertThatThrownBy(() -> userService.signup(signupRequest))
                .isInstanceOf(FindMyMemeException.class)
                .hasMessage(ErrorCode.ALREADY_EXIST_USERNAME.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
        verify(passwordEncoder, never()).encode(rawPassword);
        verify(userRepository, never()).save(any(User.class));
    }

}