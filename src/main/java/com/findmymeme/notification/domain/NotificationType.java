package com.findmymeme.notification.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public enum NotificationType {
    MEME_POST_UPLOAD_SUCCESS("이미지 게시글이 업로드 완료되었습니다."),
    POST_COMMENT("내 글에 새로운 댓글이 달렸습니다."),
    POST_ACCEPTED("당신의 답변이 채택되었습니다."),
    LIKE_RECEIVED("내 게시글에 좋아요가 추가되었습니다.");

    private final String messageTemplate;

    public String createMessage(Map<String, Object> data) {
        // 필요하면 data 활용해서 메시지 커스터마이징 가능
        return messageTemplate;
    }
}
