package com.findmymeme.user;

import com.findmymeme.user.domain.User;
import com.findmymeme.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    void findByUsername() {
        String username = "testUser";
        User user = User.builder()
                .username(username)
                .password("password")
                .email("testuser@exaple.com")
                .build();

        User findUser = userRepository.findByUsername(username).get();
        assertThat(findUser).isEqualTo(user);
    }
}