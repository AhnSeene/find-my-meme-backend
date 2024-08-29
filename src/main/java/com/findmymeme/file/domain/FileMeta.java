package com.findmymeme.file.domain;

import com.findmymeme.memepost.domain.Resolution;
import com.findmymeme.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FileMeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String originalFilename;

    @Column(nullable = false)
    private String fileUrl;

    @Column(nullable = false)
    private String extension;

    private Resolution resolution;

    @Column(nullable = false)
    private Long size;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder
    public FileMeta(String originalFilename, String fileUrl, String extension, Resolution resolution, Long size, User user) {
        this.originalFilename = originalFilename;
        this.fileUrl = fileUrl;
        this.extension = extension;
        this.resolution = resolution;
        this.size = size;
        this.user = user;
    }
}
