package com.findmymeme.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.findmymeme.notification.domain.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationSubscriber implements MessageListener {
    private final SseConnectionManager connectionManager;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            Notification notification = objectMapper.readValue(message.getBody(), Notification.class);
            connectionManager.send(notification.getUserId(), "notification", notification);
        } catch (Exception e) {
            log.error("Failed to process message from Redis and send to SSE client", e);
        }
    }
}
