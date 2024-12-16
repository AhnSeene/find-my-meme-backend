package com.findmymeme.memepost.domain;

import com.findmymeme.BaseEntity;
import com.findmymeme.tag.domain.MemePostTag;
import com.findmymeme.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private Long downloadCount = 0L;

    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "memePost")
    private List<MemePostTag> memePostTags = new ArrayList<>();

    @Builder
    public MemePost(String imageUrl, String extension, Resolution resolution, Long size, String originalFilename, User user) {
        this.imageUrl = imageUrl;
        this.extension = extension;
        this.resolution = resolution;
        this.size = size;
        this.originalFilename = originalFilename;
        this.user = user;
    }

    public void addMemePostTag(MemePostTag memePostTag) {
        this.memePostTags.add(memePostTag);
        memePostTag.changeMemePost(this);
    }

    public void removeMemePostTag(MemePostTag memePostTag) {
        this.memePostTags.remove(memePostTag);
        memePostTag.changeMemePost(null);
    }

    public List<String> getTagNames() {
        return memePostTags.stream()
                .map(mpt -> mpt.getTag().getName())
                .toList();
    }

    public boolean isOwner(User user) {
        return this.user.getId().equals(user.getId());
    }

    public void incrementLikeCount() {
        this.likeCount++;
    }

    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    public void incrementViewCount() {
        this.viewCount++;
    }

    public void incrementDownloadCount() {
        this.downloadCount++;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }
}
