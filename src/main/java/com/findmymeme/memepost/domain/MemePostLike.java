package com.findmymeme.memepost.domain;

import com.findmymeme.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"meme_post_id", "user_id"})
)
public class MemePostLike extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "meme_post_id", nullable = false)
    private Long memePostId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Builder
    public MemePostLike(Long memePostId, Long userId) {
        this.memePostId = memePostId;
        this.userId = userId;
    }
}