package com.findmymeme.findpost.repository;

import com.findmymeme.findpost.domain.FindPost;
import com.findmymeme.findpost.domain.FindStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FindPostRepository extends JpaRepository<FindPost, Long> {

    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT fp FROM FindPost fp WHERE fp.id = :id AND fp.deletedAt IS NULL")
    Optional<FindPost> findWithUserById(@Param("id") Long id);

    @Query("SELECT fp FROM FindPost fp " +
            "JOIN FETCH fp.user " +
            "JOIN FETCH fp.findPostTags fpt " +
            "JOIN FETCH fpt.tag " +
            "WHERE fp.id = :id AND fp.deletedAt IS NULL")
    Optional<FindPost> findDetailsById(@Param("id") Long id);
    @Override
    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT fp FROM FindPost fp WHERE fp.deletedAt IS NULL")
    Page<FindPost> findAll(Pageable pageable);
    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT fp FROM FindPost fp WHERE fp.deletedAt IS NULL AND fp.findStatus = :findStatus")
    Page<FindPost> findAllByFindStatus(Pageable pageable, @Param("findStatus") FindStatus findStatus);

    @Query("SELECT fp FROM FindPost fp WHERE fp.deletedAt IS NULL AND fp.user.username = :authorName")
    Page<FindPost> findAllByUsername(Pageable pageable, @Param("authorName") String authorName);

    @Query("SELECT fp FROM FindPost fp WHERE fp.deletedAt IS NULL AND " +
            "fp.user.username = :authorName AND fp.findStatus = :findStatus")
    Page<FindPost> findAllByUsernameAndFindStatus(Pageable pageable, @Param("authorName") String authorName, @Param("findStatus") FindStatus findStatus);
}
