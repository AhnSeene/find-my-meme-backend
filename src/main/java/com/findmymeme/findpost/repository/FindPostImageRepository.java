package com.findmymeme.findpost.repository;

import com.findmymeme.findpost.domain.FindPostImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FindPostImageRepository extends JpaRepository<FindPostImage, Long> {
}
