package com.findmymeme.tag.repository;

import com.findmymeme.tag.domain.MemePostTag;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemePostTagRepository extends JpaRepository<MemePostTag, Long> {

    List<MemePostTag> findAllByMemePostId(Long postId);

    @EntityGraph(attributePaths = {"tag"})
    List<MemePostTag> findTagsByMemePostIdIn(List<Long> memePostIds);
//    @Query("SELECT new com.findmymeme.memepost.repository.MemePostTagDto(mpt.memePost.id, t.name) " +
//            "FROM MemePostTag mpt " +
//            "JOIN fetch mpt.tag t " +
//            "WHERE mpt.memePost.id IN :memePostIds")
//    List<MemePostTagDto> findAllInMemePostId(List<Long> memePostIds);
}
