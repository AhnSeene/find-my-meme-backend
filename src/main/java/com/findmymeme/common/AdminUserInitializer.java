package com.findmymeme.common;

import com.findmymeme.user.dto.SignupRequest;
import com.findmymeme.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminUserInitializer implements CommandLineRunner {

    private final UserService userService;
    @Value("${admin.username}")
    private String username;

    @Value("${admin.password}")
    private String password;

    @Value("${admin.email}")
    private String email;

    @Override
    public void run(String... args) throws Exception {
        signupAdminUser();
    }

    private void signupAdminUser() {
        userService.adminSignup(SignupRequest.builder()
                .username(username)
                .password(password)
                .email(email)
                .build());
    }
}
