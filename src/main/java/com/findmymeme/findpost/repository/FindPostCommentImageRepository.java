package com.findmymeme.findpost.repository;

import com.findmymeme.findpost.domain.FindPostComment;
import com.findmymeme.findpost.domain.FindPostCommentImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.Set;

public interface FindPostCommentImageRepository extends JpaRepository<FindPostCommentImage, Long> {

    @Query("select fpci.imageUrl from FindPostCommentImage fpci where fpci.comment = :comment")
    Set<String> findImageUrlsByComment(FindPostComment comment);

    void deleteByImageUrlIn(Collection<String> imageUrls);
}
