package com.findmymeme.tag.domain;

import com.findmymeme.BaseEntity;
import com.findmymeme.findpost.domain.FindPost;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FindPostTag extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "find_post_id", nullable = false)
    private FindPost findPost;

    @Builder
    public FindPostTag(Tag tag, FindPost findPost) {
        this.tag = tag;
        this.findPost = findPost;
        findPost.addFindPostTag(this);
    }

    public void clearFindPost() {
        if (this.findPost != null) {
            this.findPost.removeFindPostTag(this);
            this.findPost = null;
        }
    }
}
