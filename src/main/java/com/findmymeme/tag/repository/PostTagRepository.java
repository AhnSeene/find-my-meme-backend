package com.findmymeme.tag.repository;

import com.findmymeme.tag.domain.PostTag;
import com.findmymeme.tag.domain.PostType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostTagRepository extends JpaRepository<PostTag, Long> {

    @EntityGraph(attributePaths = {"tag"})
    List<PostTag> findAllByPostIdAndPostType(Long postId, PostType postType);
}
