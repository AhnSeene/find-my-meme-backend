package com.findmymeme.memepost.repository;

import com.findmymeme.memepost.domain.MemePost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemePostRepository extends JpaRepository<MemePost, Long> {
}
