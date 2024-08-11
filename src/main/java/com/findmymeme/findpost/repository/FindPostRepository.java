package com.findmymeme.findpost.repository;

import com.findmymeme.findpost.domain.FindPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FindPostRepository extends JpaRepository<FindPost, Long> {
}
