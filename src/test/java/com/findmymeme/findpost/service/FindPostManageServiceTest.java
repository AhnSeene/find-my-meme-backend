package com.findmymeme.findpost.service;

import com.findmymeme.exception.ErrorCode;
import com.findmymeme.exception.FindMyMemeException;
import com.findmymeme.findpost.domain.FindPost;
import com.findmymeme.findpost.domain.FindStatus;
import com.findmymeme.findpost.dto.FindPostGetResponse;
import com.findmymeme.findpost.dto.FindPostSummaryResponse;
import com.findmymeme.findpost.repository.FindPostRepository;
import com.findmymeme.user.User;
import com.findmymeme.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class FindPostManageServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FindPostRepository findPostRepository;

    @Autowired
    private FindPostManageService findPostManageService;


    @Test
    void FindPost_단건_조회_성공() {
        // given
        String username = "newUser";
        String email = "newUser";
        String encodedPassword = "encodedPassword";

        User savedUser = userRepository.save(
                User.builder()
                        .username(username)
                        .password(encodedPassword)
                        .email(email)
                        .build()
        );

        String title = "test title";
        String content = "test content";
        String htmlContent = "<p>test content</p>";
        FindPost savedFindPost = findPostRepository.save(
                FindPost.builder()
                        .title(title)
                        .content(content)
                        .htmlContent(htmlContent)
                        .user(savedUser)
                        .build()
        );

        // when
        FindPostGetResponse response = findPostManageService.getFindPost(savedFindPost.getId(), savedUser.getId());

        //then
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo(title);
        assertThat(response.getHtmlContent()).isEqualTo(htmlContent);
        assertThat(response.getHtmlContent()).isEqualTo(htmlContent);
        assertThat(response.getStatus()).isEqualTo(FindStatus.FIND);
        assertThat(response.isOwner()).isTrue();
        assertThat(response.getCreatedAt()).isNotNull();
        assertThat(response.getUpdatedAt()).isNotNull();
    }

    @Test
    void FindPost_글의_주인이_아닌_경우_조회_성공() {
        // given
        String ownerUsername = "ownerUser";
        String ownerEmail = "ownerUser@example.com";
        String ownerPassword = "encodedPassword";

        User ownerUser = userRepository.save(
                User.builder()
                        .username(ownerUsername)
                        .password(ownerPassword)
                        .email(ownerEmail)
                        .build()
        );

        String otherUsername = "otherUser";
        String otherEmail = "otherUser@example.com";
        String otherPassword = "encodedPassword";

        User otherUser = userRepository.save(
                User.builder()
                        .username(otherUsername)
                        .password(otherPassword)
                        .email(otherEmail)
                        .build()
        );

        String title = "test title";
        String content = "test content";
        String htmlContent = "<p>test content</p>";

        FindPost savedFindPost = findPostRepository.save(
                FindPost.builder()
                        .title(title)
                        .content(content)
                        .htmlContent(htmlContent)
                        .user(ownerUser)
                        .build()
        );

        // when
        FindPostGetResponse response = findPostManageService.getFindPost(savedFindPost.getId(), otherUser.getId());

        // then
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo(title);
        assertThat(response.getHtmlContent()).isEqualTo(htmlContent);
        assertThat(response.getStatus()).isEqualTo(FindStatus.FIND);
        assertThat(response.isOwner()).isFalse();
    }

    @Test
    void FindPost_존재하지_않는_ID_조회_실패() {
        // given
        String username = "newUser";
        String email = "newUser@example.com";
        String encodedPassword = "encodedPassword";

        User savedUser = userRepository.save(
                User.builder()
                        .username(username)
                        .password(encodedPassword)
                        .email(email)
                        .build()
        );

        // when & then
        Long invalidFindPostId = 1L;
        assertThatThrownBy(() -> findPostManageService.getFindPost(invalidFindPostId, savedUser.getId()))
                .isInstanceOf(FindMyMemeException.class)
                .hasMessageContaining(ErrorCode.NOT_FOUND_FIND_POST.getMessage());
    }


    }

}