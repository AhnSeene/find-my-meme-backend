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
public class FindPost extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String htmlContent;

    @Lob
    @Column(nullable = false)
    private String content;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private FindStatus findStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

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
}
