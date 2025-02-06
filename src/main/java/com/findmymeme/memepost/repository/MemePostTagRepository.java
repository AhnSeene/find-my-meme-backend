package com.findmymeme.memepost.repository;

import com.findmymeme.memepost.dto.MemePostTagProjection;
import com.findmymeme.tag.domain.MemePostTag;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemePostTagRepository extends JpaRepository<MemePostTag, Long>, MemePostTagRepositoryCustom {

    List<MemePostTag> findAllByMemePostId(Long postId);

    @EntityGraph(attributePaths = {"tag"})
    List<MemePostTag> findTagsByMemePostIdIn(List<Long> memePostIds);

}
