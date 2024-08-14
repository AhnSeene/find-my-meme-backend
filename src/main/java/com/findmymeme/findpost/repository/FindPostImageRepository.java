package com.findmymeme.findpost.repository;

import com.findmymeme.findpost.domain.FindPost;
import com.findmymeme.findpost.domain.FindPostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface FindPostImageRepository extends JpaRepository<FindPostImage, Long> {

    @Query("select fpi.imageUrl from FindPostImage fpi where fpi.findPost = :findPost")
    Set<String> findImageUrlsByFindPost(FindPost findPost);

    void deleteByImageUrlIn(Collection<String> imageUrls);
}
