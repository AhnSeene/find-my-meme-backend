package com.findmymeme.findpost.api;

//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.findmymeme.exception.ErrorCode;
//import com.findmymeme.findpost.dto.*;
//import com.findmymeme.findpost.service.FindPostCommentReadService;
//import com.findmymeme.findpost.service.FindPostCommentWriteService;
//import com.findmymeme.response.SuccessCode;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.CsvSource;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.web.servlet.MockMvc;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.Mockito.when;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(FindPostCommentController.class)
//class FindPostCommentControllerTest {
//
//    @Autowired
//    MockMvc mockMvc;
//
//    @Autowired
//    ObjectMapper objectMapper;
//
//    @MockBean
//    FindPostCommentWriteService commentWriteService;
//
//
//    @MockBean
//    FindPostCommentReadService commentReadService;
//
//    @Test
//    @WithMockUser
//    void addComment_부모댓글이_일반_댓글_성공() throws Exception {
//        String content = "Content";
//        String htmlContent = "<p>Content</p>";
//        Long parentCommentId = null;
//
//        FindPostCommentAddRequest addRequest = FindPostCommentAddRequest.builder()
//                .content(content)
//                .htmlContent(htmlContent)
//                .parentCommentId(parentCommentId)
//                .build();
//
//        Long findPostId = 1L;
//        String username = "testUser1";
//        LocalDateTime now = LocalDateTime.now();
//        String formattedNow = now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
//        FindPostCommentAddResponse addResponse = FindPostCommentAddResponse.builder()
//                .id(1L)
//                .htmlContent(htmlContent)
//                .findPostId(findPostId)
//                .parentCommentId(parentCommentId)
//                .username(username)
//                .createdAt(now)
//                .build();
//
//        when(commentWriteService.addComment(any(FindPostCommentAddRequest.class), anyLong(), anyLong()))
//                .thenReturn(addResponse);
//
//
//        mockMvc.perform(post("/api/v1/find-posts/{postId}/comments", findPostId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(addRequest))
//                        .with(csrf())
//                )
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.message").value(SuccessCode.FIND_POST_COMMENT_UPLOAD.getMessage()))
//                .andExpect(jsonPath("$.data.id").value(1L))
//                .andExpect(jsonPath("$.data.parentCommentId").value(parentCommentId))
//                .andExpect(jsonPath("$.data.htmlContent").value(htmlContent))
//                .andExpect(jsonPath("$.data.findPostId").value(findPostId))
//                .andExpect(jsonPath("$.data.username").value(username))
//                .andExpect(jsonPath("$.data.createdAt").value(formattedNow));
//    }
//
//
//    @Test
//    @WithMockUser
//    void addComment_부모댓글이_있는_대댓글_성공() throws Exception {
//        String content = "Content";
//        String htmlContent = "<p>Content</p>";
//        Long parentCommentId = 1L;
//
//        FindPostCommentAddRequest addRequest = FindPostCommentAddRequest.builder()
//                .content(content)
//                .htmlContent(htmlContent)
//                .parentCommentId(parentCommentId)
//                .build();
//
//        Long findPostId = 1L;
//        String username = "testUser1";
//        LocalDateTime now = LocalDateTime.now();
//        String formattedNow = now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
//        FindPostCommentAddResponse addResponse = FindPostCommentAddResponse.builder()
//                .id(2L)
//                .htmlContent(htmlContent)
//                .findPostId(findPostId)
//                .parentCommentId(parentCommentId)
//                .username(username)
//                .createdAt(now)
//                .build();
//
//        when(commentWriteService.addComment(any(FindPostCommentAddRequest.class), anyLong(), anyLong()))
//                .thenReturn(addResponse);
//
//
//        mockMvc.perform(post("/api/v1/find-posts/{postId}/comments", findPostId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(addRequest))
//                        .with(csrf())
//                )
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.message").value(SuccessCode.FIND_POST_COMMENT_UPLOAD.getMessage()))
//                .andExpect(jsonPath("$.data.id").value(2L))
//                .andExpect(jsonPath("$.data.parentCommentId").value(parentCommentId))
//                .andExpect(jsonPath("$.data.htmlContent").value(htmlContent))
//                .andExpect(jsonPath("$.data.findPostId").value(findPostId))
//                .andExpect(jsonPath("$.data.username").value(username))
//                .andExpect(jsonPath("$.data.createdAt").value(formattedNow));
//    }
//
//
//
//    @WithMockUser
//    @ParameterizedTest
//    @CsvSource({
//            "'', Content",
//            "<p>Content</p>, ''",
//    })
//    void addComment_유효성검사_실패(String htmlContent, String content) throws Exception {
//
//        FindPostCommentAddRequest invalidRequest = FindPostCommentAddRequest.builder()
//                .htmlContent(htmlContent)
//                .content(content)
//                .parentCommentId(null)
//                .build();
//
//        mockMvc.perform(post("/api/v1/find-posts/{postId}/comments", 1L)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(invalidRequest))
//                        .with(csrf())
//                )
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.success").value(false))
//                .andExpect(jsonPath("$.message").value(ErrorCode.INVALID_INPUT_VALUE.getMessage()));
//    }
//
//    @Test
//    @WithMockUser
//    void getComment_단건조회_성공() throws Exception {
//        // given
//        String htmlContent = "<p>Content</p>";
//        Long parentCommentId = 1L;
//        Long findPostId = 1L;
//        String username = "testUser1";
//        LocalDateTime now = LocalDateTime.now();
//        String formattedNow = now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
//
//        FindPostCommentGetResponse getResponse = FindPostCommentGetResponse.builder()
//                .id(1L)
//                .findPostId(findPostId)
//                .parentCommentId(parentCommentId)
//                .username(username)
//                .htmlContent(htmlContent)
//                .owner(true)
//                .createdAt(now)
//                .build();
//
//        // when
//        when(commentReadService.getComment(1L, 1L, 1L))
//                .thenReturn(getResponse);
//
//        // then
//        mockMvc.perform(get("/api/v1/find-posts/{postId}/comments/{commentId}", 1L, 1L)
//                        .accept(MediaType.APPLICATION_JSON)
//                        .with(csrf())
//                )
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.message").value(SuccessCode.FIND_POST_COMMENT_GET.getMessage()))
//                .andExpect(jsonPath("$.data.htmlContent").value(htmlContent))
//                .andExpect(jsonPath("$.data.parentCommentId").value(parentCommentId))
//                .andExpect(jsonPath("$.data.findPostId").value(findPostId))
//                .andExpect(jsonPath("$.data.owner").value(true))
//                .andExpect(jsonPath("$.data.username").value(username))
//                .andExpect(jsonPath("$.data.createdAt").value(formattedNow));
//    }
//
//
//    @Test
//    @WithMockUser
//    void updateFindPost_이미지_없을때_성공() throws Exception {
//
//        String content = "Updated Content";
//        String htmlContent = "<p>Updated Content</p>";
//
//        FindPostCommentUpdateRequest updateRequest = FindPostCommentUpdateRequest.builder()
//                .content(content)
//                .htmlContent(htmlContent)
//                .build();
//
//        Long findPostId = 1L;
//        Long parentCommentId = null;
//        String username = "testUser1";
//        LocalDateTime now = LocalDateTime.now();
//        String formattedNow = now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
//        FindPostCommentUpdateResponse updateResponse = FindPostCommentUpdateResponse.builder()
//                .id(1L)
//                .htmlContent(htmlContent)
//                .findPostId(findPostId)
//                .parentCommentId(parentCommentId)
//                .username(username)
//                .createdAt(now)
//                .build();
//
//        when(commentWriteService.updateComment(any(FindPostCommentUpdateRequest.class), anyLong(), anyLong(), anyLong()))
//                .thenReturn(updateResponse);
//
//        // when
//        mockMvc.perform(put("/api/v1/find-posts/{postId}/comments/{commentId}", 1L, 1L)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(new ObjectMapper().writeValueAsString(updateRequest))
//                        .accept(MediaType.APPLICATION_JSON)
//                        .with(csrf())
//                )
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.message").value(SuccessCode.FIND_POST_COMMENT_UPDATE.getMessage()))
//                .andExpect(jsonPath("$.data.id").value(1L))
//                .andExpect(jsonPath("$.data.parentCommentId").value(parentCommentId))
//                .andExpect(jsonPath("$.data.htmlContent").value(htmlContent))
//                .andExpect(jsonPath("$.data.findPostId").value(findPostId))
//                .andExpect(jsonPath("$.data.username").value(username))
//                .andExpect(jsonPath("$.data.createdAt").value(formattedNow));
//    }
//
//    @WithMockUser
//    @ParameterizedTest
//    @CsvSource({
//            "'', Content",
//            "<p>Content</p>, ''",
//    })
//    void update_유효성검사_실패(String htmlContent, String content) throws Exception {
//
//        FindPostCommentUpdateRequest invalidRequest = FindPostCommentUpdateRequest.builder()
//                .htmlContent(htmlContent)
//                .content(content)
//                .build();
//
//        mockMvc.perform(put("/api/v1/find-posts/{postId}/comments/{commetId}", 1L, 1L)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(invalidRequest))
//                        .with(csrf())
//                )
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.success").value(false))
//                .andExpect(jsonPath("$.message").value(ErrorCode.INVALID_INPUT_VALUE.getMessage()));
//    }
//
//}