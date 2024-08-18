package com.findmymeme.memepost.domain;

import com.findmymeme.BaseEntity;
import com.findmymeme.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemePost extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private String extension;

    private Resolution resolution;

    @Column(nullable = false)
    private Long size;

    @Column(nullable = false)
    private String originalFilename;

    @Column(nullable = false)
    private Long likeCount;

    @Column(nullable = false)
    private Long viewCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder
    public MemePost(String imageUrl, String extension, Resolution resolution, Long size, String originalFilename, User user) {
        this.imageUrl = imageUrl;
        this.extension = extension;
        this.resolution = resolution;
        this.size = size;
        this.originalFilename = originalFilename;
        this.likeCount = 0L;
        this.viewCount = 0L;
        this.user = user;
    }

    public boolean isOwner(User user) {
        return this.user.getId().equals(user.getId());
    }
}
