package com.findmymeme.user;

import com.findmymeme.exception.ErrorCode;
import com.findmymeme.exception.FindMyMemeException;
import com.findmymeme.user.dto.SignupRequest;
import com.findmymeme.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse signup(SignupRequest signupRequest) {
        userRepository.findByUsername(signupRequest.getUsername()).ifPresent(user -> {
            throw new FindMyMemeException(ErrorCode.ALREADY_EXIST_USERNAME);
        });
        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());
        User user = SignupRequest.toEntity(signupRequest, encodedPassword);
        return new UserResponse(userRepository.save(user));
    }
}
