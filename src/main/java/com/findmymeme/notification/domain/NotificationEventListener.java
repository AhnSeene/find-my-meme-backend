package com.findmymeme.notification.domain;

import com.findmymeme.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.embedded.netty.NettyWebServer;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {
    private final NotificationService notificationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handle(MemePostUploadSuccessEvent event) {
        log.info("Handling MemePostUploadSuccessEvent for userId: {}, memePostId: {}", event.userId(), event.memePostId());
        notificationService.createNotification(
                event.userId(),
                NotificationType.MEME_POST_UPLOAD_SUCCESS,
                Map.of("memePostId", event.memePostId())
        );
    }
}
