package com.findmymeme.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.findmymeme.exception.ErrorCode;
import com.findmymeme.response.SuccessCode;
import com.findmymeme.user.dto.SignupRequest;
import com.findmymeme.user.dto.SignupResponse;
import com.findmymeme.user.service.UserService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void 회원가입_성공_유효성검사_통과() throws Exception {
        // given
        SignupRequest signupRequest = SignupRequest.builder()
                .username("testuser")
                .password("Test1234!")
                .email("test@example.com")
                .build();

        SignupResponse signupResponse = new SignupResponse("testuser", "test@example.com");

        // when
        when(userService.signup(any(SignupRequest.class))).thenReturn(signupResponse);

        // then
        mockMvc.perform(post("/api/v1/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.message").value(SuccessCode.USER_SIGNUP.getMessage()))
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"));

    }

    @ParameterizedTest
    @DisplayName("회원가입 실패 시: 유효성 검사를 통과하지 못한다")
    @CsvSource({
            "'', Test1234!, test@example.com, username",          // 빈 username
            "testuser, '', test@example.com, password",        // 빈 password
            "testuser, short, test@example.com, password",     // password 길이 짧음
            "testuser, Test1234!, '', email",                  // 빈 email
            "testuser, Test1234!, invalid-email, email",      // email 형식 틀림
            "test, Test1234!, test@example.com, username",        // 아이디 길이
            "testuser, test1234, test@example.com, password",     // password 형식틀림
            "testuser, testuser!, test@example.com, password",     // password 형식틀림
    })
    void 회원가입_유효성검사_실패(String username, String password, String email, String expectedField) throws Exception {
        SignupRequest signupRequest = new SignupRequest(username, password, email);

        mockMvc.perform(post("/api/v1/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(ErrorCode.REQUEST_INVALID_INPUT.getMessage()))
                .andExpect(jsonPath("$.data[0].field").value(expectedField)); // 에러가 발생한 필드 이름을 직접 비교
    }

}
