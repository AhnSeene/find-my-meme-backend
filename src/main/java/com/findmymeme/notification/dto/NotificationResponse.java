package com.findmymeme.notification.dto;

import com.findmymeme.notification.domain.Notification;
import com.findmymeme.notification.domain.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Schema(description = "알림 정보 응답 DTO")
@Getter
public class NotificationResponse {

    @Schema(description = "알림 ID", example = "1")
    private final Long id;

    @Schema(description = "알림 타입", example = "MEME_POST_UPLOAD_SUCCESS")
    private final NotificationType type;

    @Schema(description = "알림 메시지", example = "밈 게시글(ID:101) 업로드가 완료되었습니다.")
    private final String message;

    @Schema(description = "읽음 여부", example = "false")
    private final boolean isRead;

    @Schema(description = "알림 관련 추가 데이터 (e.g., 이동할 게시글 ID)", example = "{\"memePostId\": 101}")
    private final Map<String, Object> data;

    @Schema(description = "알림 생성 일시", example = "2023-10-27T10:00:00")
    private final LocalDateTime createdAt;

    @Builder
    private NotificationResponse(Long id, NotificationType type, String message, boolean isRead, Map<String, Object> data, LocalDateTime createdAt) {
        this.id = id;
        this.type = type;
        this.message = message;
        this.isRead = isRead;
        this.data = data;
        this.createdAt = createdAt;
    }

    public static NotificationResponse from(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .type(notification.getType())
                .message(notification.getMessage())
                .isRead(notification.isRead())
                .data(notification.getData())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}