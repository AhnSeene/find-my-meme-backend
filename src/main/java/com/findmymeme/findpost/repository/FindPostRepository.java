package com.findmymeme.findpost.repository;

import com.findmymeme.findpost.domain.FindPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FindPostRepository extends JpaRepository<FindPost, Long> {

    @EntityGraph(attributePaths = {"user"})
    Optional<FindPost> findWithUserById(Long id);

    @Override
    @EntityGraph(attributePaths = {"user"})
    Page<FindPost> findAll(Pageable pageable);
}
