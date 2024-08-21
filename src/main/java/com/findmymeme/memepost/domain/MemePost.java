package com.findmymeme.memepost.domain;

import com.findmymeme.BaseEntity;
import com.findmymeme.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDateTime;

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
    private Long likeCount = 0L;

    @Column(nullable = false)
    private Long viewCount = 0L;

    @Column(nullable = false)
    private LocalDateTime deletedAt;

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
        this.user = user;
    }

    public boolean isOwner(User user) {
        return this.user.getId().equals(user.getId());
    }

    public void incrementLikeCount() {
        this.likeCount++;
    }

    public void decrementLikeCount() {
        this.likeCount--;
    }

    public void incrementViewCount() {
        this.viewCount++;
    }

    public void decrementViewCount() {
        this.viewCount--;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }
}
