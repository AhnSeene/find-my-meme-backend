package com.findmymeme.memepost.service;

import com.findmymeme.memepost.dto.ImageResizerPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class MemePostEventListener {

    private final LambdaService lambdaService;

//    @Retryable(
//            value = { RuntimeException.class }, // Lambda 호출 실패 시 재시도
//            maxAttempts = 3,
//            backoff = @Backoff(delay = 2000)
//    )
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMemePostCreation(MemePostCreatedEvent event) {
        lambdaService.invokeImageResizeLambda(event);
    }
}
