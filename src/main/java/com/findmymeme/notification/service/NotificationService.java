package com.findmymeme.notification.service;

import com.findmymeme.notification.domain.Notification;
import com.findmymeme.notification.domain.NotificationConstants;
import com.findmymeme.notification.domain.NotificationType;
import com.findmymeme.notification.dto.NotificationResponse;
import com.findmymeme.notification.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.findmymeme.notification.domain.NotificationConstants.NOTIFICATION_CHANNEL_PREFIX;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {
    private final NotificationRepository notificationRepository;
    @Qualifier("notificationRedisTemplate")
    private final RedisTemplate<String, Object> redisTemplate;

    public void createNotification(Long userId, NotificationType type, Map<String, Object> data) {
        log.info("Creating notification for userId: {}", userId);
        String message = type.createMessage(data);

        Notification notification = Notification.builder()
                .userId(userId)
                .type(type)
                .message(message)
                .data(data)
                .build();

        notificationRepository.save(notification);

        redisTemplate.convertAndSend(NOTIFICATION_CHANNEL_PREFIX + userId, notification);
        log.info("Published notification event to Redis for userId: {}", userId);
    }

    public List<NotificationResponse> getNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(NotificationResponse::from)
                .collect(Collectors.toList());
    }
}


