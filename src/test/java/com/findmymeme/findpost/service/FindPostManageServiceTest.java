package com.findmymeme.findpost.service;

import com.findmymeme.findpost.domain.FindPost;
import com.findmymeme.findpost.domain.FindStatus;
import com.findmymeme.findpost.dto.FindPostGetResponse;
import com.findmymeme.findpost.repository.FindPostRepository;
import com.findmymeme.user.User;
import com.findmymeme.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

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
        FindPost savedFindPost = findPostRepository.save(
                FindPost.builder()
                        .title(title)
                        .content(content)
                        .user(savedUser)
                        .build()
        );

        // when
        FindPostGetResponse response = findPostManageService.getFindPost(savedFindPost.getId(), savedUser.getId());

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo(title);
        assertThat(response.getContent()).isEqualTo(content);
        assertThat(response.getStatus()).isEqualTo(FindStatus.FIND);
        assertThat(response.isOwner()).isTrue();

    }

}