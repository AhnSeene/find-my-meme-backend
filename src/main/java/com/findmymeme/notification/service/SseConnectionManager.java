package com.findmymeme.notification.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class SseConnectionManager {

    private static final Long EMITTER_EXPIRATION_TIME = TimeUnit.HOURS.toMillis(1);
    private static final Long HEARTBEAT_INTERVAL = TimeUnit.SECONDS.toMillis(30);
    private final Map<Long, SseEmitter> connections = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();


    @PostConstruct
    public void init() {
        // 모든 연결에 heartbeat 전송
        scheduler.scheduleAtFixedRate(this::sendHeartbeatToAll, 0, HEARTBEAT_INTERVAL, TimeUnit.MILLISECONDS);
    }

    public SseEmitter addConnection(Long userId) {
        SseEmitter emitter = new SseEmitter(EMITTER_EXPIRATION_TIME);
        connections.put(userId, emitter);
        log.info("New SSE connection added for userId: {}. Total connections: {}", userId, connections.size());

        emitter.onCompletion(() -> {
            connections.remove(userId);
            log.info("SSE connection completed for userId: {}. Total connections: {}", userId, connections.size());
        });
        emitter.onError(throwable -> {
            log.error("SSE error for userId: {}", userId, throwable);
            emitter.complete();
        });
        emitter.onTimeout(() -> {
            log.info("SSE connection timed out for userId: {}", userId);
            emitter.complete();
        });

        // 최초 연결 시 더미 데이터 전송
        send(userId, "sse-connection-success", Map.of("message", "Connected!"));

        return emitter;
    }

    public void send(Long userId, String eventName, Object data) {
        SseEmitter emitter = connections.get(userId);
        if (emitter != null) {
            try {
                emitter.send(
                        SseEmitter.event()
                                .name(eventName)
                                .data(data));
            } catch (IOException e) {
                log.warn("Failed to send SSE data for userId: {}. Removing connection.", userId, e);
                emitter.complete();
            }
        }
    }

    private void sendHeartbeatToAll() {
        if (connections.isEmpty()) {
            return;
        }
        log.trace("Sending heartbeat to {} connections.", connections.size());
        connections.forEach((userId, emitter) -> {
            try {
                emitter.send(SseEmitter.event().name("heartbeat").comment("keep-alive"));
            } catch (IOException e) {
                log.warn("Failed to send heartbeat to userId: {}. Removing connection.", userId, e);
                emitter.complete();
            }
        });
    }
}
