package com.findmymeme.tag.domain;

import com.findmymeme.BaseEntity;
import com.findmymeme.memepost.domain.MemePost;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemePostTag extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meme_post_id", nullable = false)
    private MemePost memePost;

    @Builder
    public MemePostTag(Tag tag, MemePost memePost) {
        this.tag = tag;
        this.memePost = memePost;
    }

    public void changeMemePost(MemePost memePost) {
        this.memePost = memePost;
    }
}
