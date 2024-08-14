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
public class FindPostImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private String originalFilename;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "find_post_id", nullable = false)
    private FindPost findPost;

    @Builder
    public FindPostImage(String imageUrl, String originalFilename, FindPost findPost) {
        this.imageUrl = imageUrl;
        this.originalFilename = originalFilename;
        this.findPost = findPost;
    }

}
