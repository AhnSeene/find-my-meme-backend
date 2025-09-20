package com.findmymeme.notification.domain;

import com.findmymeme.BaseEntity;
import com.findmymeme.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    private String message;

    private boolean isRead;

    /**
     * 알림별 커스텀 데이터를 저장하기 위해 JSON 컬럼 사용 - 프론트에서 랜더링할 때 필요한 최소 데이터만 저장
     */
    @Column(columnDefinition = "json")
    @Convert(converter = JsonDataConverter.class)
    private Map<String, Object> data;

    @Builder
    public Notification(Long userId, NotificationType type, String message, boolean isRead, Map<String, Object> data) {
        this.userId = userId;
        this.type = type;
        this.message = message;
        this.isRead = isRead;
        this.data = data;
    }

    public void markAsRead() {
        this.isRead = true;
    }

}
