package com.findmymeme.findpost.domain;

import com.findmymeme.BaseEntity;
import com.findmymeme.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FindPost extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String htmlContent;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private FindStatus findStatus;

    @Column(nullable = false)
    private Long viewCount = 0L;

    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_comment_id")
    private FindPostComment selectedComment;

    @Builder
    public FindPost(String title, String htmlContent, String content, User user) {
        this.title = title;
        this.htmlContent = htmlContent;
        this.content = content;
        this.findStatus = FindStatus.FIND;
        this.user = user;
    }

    public boolean isOwner(User user) {
        return this.user.getId().equals(user.getId());
    }

    public void changeTitle(String title) {
        this.title = title;
    }

    public void changeHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
    }

    public void changeContent(String content) {
        this.content = content;
    }

    public void incrementViewCount() {
        this.viewCount++;
    }

    public void decrementViewCount() {
        this.viewCount--;
    }

    public void foundByComment(FindPostComment comment) {
        this.findStatus = FindStatus.FOUND;
        this.selectedComment = comment;
        comment.selected();
    }

    public boolean isFound() {
        return this.findStatus.equals(FindStatus.FOUND);
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }
}
