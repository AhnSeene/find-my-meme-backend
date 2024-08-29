package com.findmymeme.findpost.repository;

//import com.findmymeme.findpost.domain.FindPost;
//import com.findmymeme.findpost.domain.FindPostComment;
//import com.findmymeme.user.domain.User;
//import com.findmymeme.user.repository.UserRepository;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
//
//import java.util.List;
//
//@DataJpaTest
//@EnableJpaAuditing
//class FindPostCommentRepositoryTest {
//
//    @Autowired
//    UserRepository userRepository;
//    @Autowired FindPostRepository findPostRepository;
//
//    @Autowired FindPostCommentRepository findPostCommentRepository;
//    @Test
//    void findAllCommentsAndReplies() {
//        User user = User.builder()
//                .username("username2")
//                .password("dkfjwkfkdfj")
//                .email("email@urename")
//                .build();
//
//        FindPost findPost = FindPost.builder()
//                .title("title")
//                .content("content")
//                .htmlContent("<p>content</>")
//                .user(user)
//                .build();
//        FindPostComment findPostComment = FindPostComment
//                .builder()
//                .findPost(findPost)
//                .content("댓글 1")
//                .htmlContent("<p> 댓글 1 </p>")
//                .user(user)
//                .build();
//
//        FindPostComment findPostComment2 = FindPostComment
//                .builder()
//                .findPost(findPost)
//                .parentComment(findPostComment)
//                .content("댓글 1-1")
//                .htmlContent("<p> 댓글 1-1 </p>")
//                .user(user)
//                .build();
//
//        FindPostComment findPostComment3 = FindPostComment
//                .builder()
//                .findPost(findPost)
//                .parentComment(findPostComment)
//                .content("댓글 1-2")
//                .htmlContent("<p> 댓글 1-2 </p>")
//                .user(user)
//                .build();
//
//        FindPostComment findPostComment4 = FindPostComment
//                .builder()
//                .findPost(findPost)
//                .content("댓글 2")
//                .htmlContent("<p> 댓글 2 </p>")
//                .user(user)
//                .build();
//
//        FindPostComment findPostComment5 = FindPostComment
//                .builder()
//                .findPost(findPost)
//                .content("댓글 3")
//                .htmlContent("<p> 댓글 3 </p>")
//                .user(user)
//                .build();
//
//        FindPostComment findPostComment6 = FindPostComment
//                .builder()
//                .findPost(findPost)
//                .parentComment(findPostComment5)
//                .content("댓글 3-1")
//                .htmlContent("<p> 댓글 3-1 </p>")
//                .user(user)
//                .build();
//
//        userRepository.save(user);
//        findPostRepository.save(findPost);
//        findPostCommentRepository.save(findPostComment);
//        findPostCommentRepository.save(findPostComment2);
//        findPostCommentRepository.save(findPostComment3);
//        findPostCommentRepository.save(findPostComment4);
//        findPostCommentRepository.save(findPostComment5);
//        findPostCommentRepository.save(findPostComment6);
//
//        List<FindPostComment> result = findPostCommentRepository.findAllCommentsAndReplies(findPost.getId());
//        Assertions.assertThat(result.get(0).getId()).isEqualTo(findPostComment.getId());
//        Assertions.assertThat(result.get(1).getId()).isEqualTo(findPostComment4.getId());
//        Assertions.assertThat(result.get(2).getId()).isEqualTo(findPostComment5.getId());
//        Assertions.assertThat(result.get(3).getId()).isEqualTo(findPostComment2.getId());
//        Assertions.assertThat(result.get(4).getId()).isEqualTo(findPostComment3.getId());
//        Assertions.assertThat(result.get(5).getId()).isEqualTo(findPostComment6.getId());
//    }
//}