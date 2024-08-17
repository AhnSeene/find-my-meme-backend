package com.findmymeme.tag.repository;

import com.findmymeme.tag.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {

    @Query("select t1 FROM Tag t1 left join fetch t1.subTags t2 where t1.parentTag is null")
    List<Tag> findAllTagsWithSubTags();
}
