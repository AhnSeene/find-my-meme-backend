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

    @Test
    @WithMockUser
    void getFindPost_단건조회_성공() throws Exception {
        // given
        String htmlContent = "<p>Content</p>";
        String title = "Title";
        String username = "testUser";
        LocalDateTime now = LocalDateTime.now();
        String formattedNow = now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        FindPostGetResponse getResponse = FindPostGetResponse.builder()
                .title(title)
                .htmlContent(htmlContent)
                .status(FindStatus.FIND)
                .username(username)
                .owner(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // when
        when(findPostReadService.getFindPost(1L, 1L)).thenReturn(getResponse);

        // then
        mockMvc.perform(get("/api/v1/find-posts/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(SuccessCode.FIND_POST_GET.getMessage()))
                .andExpect(jsonPath("$.data.title").value(title))
                .andExpect(jsonPath("$.data.htmlContent").value(htmlContent))
                .andExpect(jsonPath("$.data.owner").value(true))
                .andExpect(jsonPath("$.data.username").value(username))
                .andExpect(jsonPath("$.data.status").value(FindStatus.FIND.name()))
                .andExpect(jsonPath("$.data.createdAt").value(formattedNow))
                .andExpect(jsonPath("$.data.updatedAt").value(formattedNow));
    }

    @Test
    @WithMockUser
    void getFindPosts_페이지_조회_성공() throws Exception {
        // given
        List<FindPostSummaryResponse> findPostSummaryList = Arrays.asList(
                FindPostSummaryResponse.builder()
                        .title("Title 1")
                        .content("Content 1")
                        .status(FindStatus.FIND)
                        .username("User1")
                        .createdAt(LocalDateTime.of(2024, 8, 14, 2, 30, 34))
                        .build(),
                FindPostSummaryResponse.builder()
                        .title("Title 2")
                        .content("Content 2")
                        .status(FindStatus.FOUND)
                        .username("User2")
                        .createdAt(LocalDateTime.of(2024, 8, 14, 2, 35, 34))
                        .build()
        );

        Page<FindPostSummaryResponse> page = new PageImpl<>(findPostSummaryList, PageRequest.of(0, 10), 2);

        when(findPostReadService.getFindPosts(0, 10)).thenReturn(page);

        // when
        mockMvc.perform(get("/api/v1/find-posts")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(SuccessCode.FIND_POST_GET.getMessage()))
                .andExpect(jsonPath("$.data.content[0].title").value("Title 1"))
                .andExpect(jsonPath("$.data.content[0].content").value("Content 1"))
                .andExpect(jsonPath("$.data.content[0].status").value(FindStatus.FIND.name()))
                .andExpect(jsonPath("$.data.content[0].username").value("User1"))
                .andExpect(jsonPath("$.data.content[0].createdAt").value("2024-08-14T02:30:34"))
                .andExpect(jsonPath("$.data.content[1].title").value("Title 2"))
                .andExpect(jsonPath("$.data.content[1].content").value("Content 2"))
                .andExpect(jsonPath("$.data.content[1].status").value(FindStatus.FOUND.name()))
                .andExpect(jsonPath("$.data.content[1].username").value("User2"))
                .andExpect(jsonPath("$.data.content[1].createdAt").value("2024-08-14T02:35:34"))
                .andExpect(jsonPath("$.data.first").value(true))
                .andExpect(jsonPath("$.data.last").value(true))
                .andExpect(jsonPath("$.data.hasNext").value(false))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(jsonPath("$.data.totalElements").value(2))
                .andExpect(jsonPath("$.data.page").value(1))
                .andExpect(jsonPath("$.data.size").value(10));
    }

}