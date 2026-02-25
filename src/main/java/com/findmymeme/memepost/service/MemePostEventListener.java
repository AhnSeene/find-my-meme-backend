package com.findmymeme.memepost.service;

import com.findmymeme.memepost.dto.ImageResizerPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemePostEventListener {

    private final ImageProcessingService imageProcessingService;
    private final MemePostService memePostService;

//    @Retryable(
//            value = { RuntimeException.class }, // SQS 전송 실패 시 재시도
//            maxAttempts = 3,
//            backoff = @Backoff(delay = 2000)
//    )
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMemePostCreation(MemePostCreatedEvent event) {
        try {
            imageProcessingService.sendImageProcessingRequest(event);
        } catch (Exception e) {
            log.error("SQS 메시지 전송 실패. memePostId={}, userId={}", event.getMemePostId(), event.getUserId(), e);
            memePostService.updatePostToFailed(event.getMemePostId(), event.getUserId(), e.getMessage());
        }
    }
}
