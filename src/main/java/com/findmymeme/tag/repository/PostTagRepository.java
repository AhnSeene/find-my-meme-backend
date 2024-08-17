package com.findmymeme.tag.repository;

import com.findmymeme.tag.domain.PostTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostTagRepository extends JpaRepository<PostTag, Long> {
}
