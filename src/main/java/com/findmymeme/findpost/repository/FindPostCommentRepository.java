package com.findmymeme.findpost.repository;

import com.findmymeme.findpost.domain.FindPost;
import com.findmymeme.findpost.domain.FindPostComment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FindPostCommentRepository extends JpaRepository<FindPostComment, Long> {
    @EntityGraph(attributePaths = {"user"})
    Optional<FindPostComment> findWithUserById(Long id);

    @Query("select c from FindPostComment c left join fetch c.user " +
            "where c.findPost = :findPost and c.parentComment is null")
    List<FindPostComment> findComments(FindPost findPost);

    @Query("SELECT c FROM FindPostComment c LEFT JOIN FETCH c.user " +
            "LEFT JOIN FETCH c.parentComment pc " +
            "WHERE c.findPost.id = :postId " +
            "order by c.parentComment.id, c.id")
    List<FindPostComment> findAllCommentsAndReplies(Long postId);

}
