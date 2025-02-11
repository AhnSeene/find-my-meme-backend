package com.findmymeme.tag.repository;

import com.findmymeme.findpost.domain.FindPostTag;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FindPostTagRepository extends JpaRepository<FindPostTag, Long> {

    @EntityGraph(attributePaths = {"tag"})
    List<FindPostTag> findAllByFindPostId(Long findPostId);
}
