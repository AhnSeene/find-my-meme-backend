package com.findmymeme.findpost.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.findmymeme.exception.ErrorCode;
import com.findmymeme.findpost.domain.FindStatus;
import com.findmymeme.findpost.dto.*;
import com.findmymeme.findpost.service.FindPostReadService;
import com.findmymeme.findpost.service.FindPostWriteService;
import com.findmymeme.response.SuccessCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(FindPostController.class)
class FindPostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FindPostWriteService findPostWriteService;

    @MockBean
    private FindPostReadService findPostReadService;

    @Test
    @WithMockUser
    void upload_성공() throws Exception {
        // given
        String content = "Content";
        String htmlContent = "<p>Content</p>";
        String title = "Title";

        FindPostUploadRequest uploadRequest = FindPostUploadRequest.builder()
                .title(title)
                .htmlContent(htmlContent)
                .content(content)
                .build();
        FindPostUploadResponse uploadResponse = FindPostUploadResponse.builder()
                .id(1L)
                .title(title)
                .content(content)
                .status(FindStatus.FIND)
                .build();

        // when
        when(findPostWriteService.uploadFindPost(any(FindPostUploadRequest.class), anyLong()))
                .thenReturn(uploadResponse);

        // then
        mockMvc.perform(post("/api/v1/find-posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(uploadRequest))
                        .with(csrf())
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(SuccessCode.FIND_POST_UPLOAD.getMessage()))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.title").value(title))
                .andExpect(jsonPath("$.data.content").value(content))
                .andExpect(jsonPath("$.data.status").value(FindStatus.FIND.name()));
    }


    @WithMockUser
    @ParameterizedTest
    @CsvSource({
            "'', <p>Content</p>, Content",
            "Title, '', Content",
            "Title, <p>Content</p>, ''",
    })
    void upload_유효성검사_실패(String title, String htmlContent, String content) throws Exception {

        FindPostUploadRequest invalidRequest = FindPostUploadRequest.builder()
                .title(title)
                .htmlContent(htmlContent)
                .content(content)
                .build();

        mockMvc.perform(post("/api/v1/find-posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest))
                        .with(csrf())
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(ErrorCode.INVALID_INPUT_VALUE.getMessage()));
    }
}