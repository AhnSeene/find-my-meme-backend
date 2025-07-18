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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProcessingStatus processingStatus;


    @Column(name = "thumbnail288_url", length = 512)
    private String thumbnail288Url;

    @Column(name = "thumbnail657_url", length = 512)
    private String thumbnail657Url;

    @Convert(converter = ExtensionConverter.class)
    @Column(nullable = false)
    private Extension extension;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MediaType mediaType;

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
    public MemePost(String imageUrl, String extension, Resolution resolution, Long size, String originalFilename,
                    User user, String thumbnail288Url, String thumbnail657Url, ProcessingStatus processingStatus) {
        this.imageUrl = imageUrl;
        this.extension = Extension.from(extension);
        this.mediaType = MediaType.fromExtension(extension);
        this.resolution = resolution;
        this.size = size;
        this.originalFilename = originalFilename;
        this.user = user;
        this.thumbnail288Url = thumbnail288Url;
        this.thumbnail657Url = thumbnail657Url;
        this.processingStatus = (processingStatus != null) ? processingStatus :ProcessingStatus.PROCESSING;
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

    public boolean isOwner(Long userId) {
        return this.user.getId().equals(userId);
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

    public void changeProcessingStatus(ProcessingStatus status) {
        this.processingStatus = status;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }
}
