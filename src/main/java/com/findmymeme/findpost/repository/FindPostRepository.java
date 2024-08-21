package com.findmymeme.findpost.repository;

import com.findmymeme.findpost.domain.FindPost;
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

    @Override
    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT fp FROM FindPost fp WHERE fp.deletedAt IS NULL")
    Page<FindPost> findAll(Pageable pageable);
}
