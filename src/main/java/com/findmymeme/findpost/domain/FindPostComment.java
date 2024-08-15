package com.findmymeme.findpost.domain;

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
public class FindPostComment extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String htmlContent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private FindPostComment parentComment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "find_post_id", nullable = false)
    private FindPost findPost;

    @Builder
    public FindPostComment(String content, String htmlContent, FindPostComment parentComment, User user, FindPost findPost) {
        this.content = content;
        this.htmlContent = htmlContent;
        this.parentComment = parentComment;
        this.user = user;
        this.findPost = findPost;
    }

    public boolean isOwner(User user) {
        return this.user.getId().equals(user.getId());
    }

    public void changeHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
    }

    public void changeContent(String content) {
        this.content = content;
    }
}
