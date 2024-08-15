package com.findmymeme.findpost.domain;

import com.findmymeme.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FindPostCommentImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private String originalFilename;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private FindPostComment comment;

    @Builder
    public FindPostCommentImage(String imageUrl, String originalFilename, FindPostComment comment) {
        this.imageUrl = imageUrl;
        this.originalFilename = originalFilename;
        this.comment = comment;
    }
}
