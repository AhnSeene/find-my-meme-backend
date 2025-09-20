package com.findmymeme.memepost.service;

import com.findmymeme.exception.ErrorCode;
import com.findmymeme.exception.FindMyMemeException;
import com.findmymeme.memepost.dto.ImageCompletionDto;
import io.awspring.cloud.sqs.annotation.SqsListener;
import io.awspring.cloud.sqs.listener.acknowledgement.Acknowledgement;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * SQS 메시지를 수신하고 처리하는 메인 리스너
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ImageCompletionListener {

    private final MemePostService memePostService;
    private final Validator validator;

    @Transactional
    @SqsListener("${aws.sqs.image-completion-queue}")
    public void handleImageCompletion(@Payload ImageCompletionDto dto, Acknowledgement ack) {
        log.info("SQS message received. postId={}, status={}", dto.getMemePostId(), dto.getStatus());

        try {
            validateDto(dto);

            if ("SUCCESS".equals(dto.getStatus())) {
                processSuccess(dto);
            } else {
                processFailure(dto, "Invalid status");
            }

            ack.acknowledge();
            log.info("Message acknowledged. postId={}", dto.getMemePostId());

        } catch (Exception e) {
            handleError(dto, e, ack);
        }
    }

    private void validateDto(ImageCompletionDto dto) {
        Set<ConstraintViolation<ImageCompletionDto>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            String errorMessage = violations.stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .collect(Collectors.joining(", "));
            throw new FindMyMemeException(errorMessage, ErrorCode.REQUEST_INVALID_INPUT);
        }
    }

    private void processSuccess(ImageCompletionDto dto) {
        memePostService.updatePostAfterProcessing(dto.getMemePostId(), dto.getUserId(), dto.getThumbnail288Url(), dto.getThumbnail657Url());
        log.info("Post updated successfully. postId={}", dto.getMemePostId());
    }

    private void processFailure(ImageCompletionDto dto, String errorMessage) {
        memePostService.updatePostToFailed(dto.getMemePostId(), dto.getUserId(), errorMessage);
        log.warn("Failure status message processed. postId={}, reason={}", dto.getMemePostId(), errorMessage);
    }

    private void handleError(ImageCompletionDto dto, Throwable e, Acknowledgement ack) {
        Throwable rootCause = getRootCause(e);

        if (isNonRetryable(rootCause)) {
            log.warn("Non-retryable error. Message will be acknowledged. postId={}, reason={}",
                    dto.getMemePostId(), rootCause.getMessage());
            if (dto.getMemePostId() != null) {
                memePostService.updatePostToFailed(dto.getMemePostId(), dto.getUserId(), rootCause.getMessage());
            }
            ack.acknowledge();
        } else {
            log.error("Retryable error occurred. Message will be retried. postId={}, error={}",
                    dto.getMemePostId(), rootCause.getMessage(), rootCause);
            throw new RuntimeException(rootCause);
        }
    }

    private boolean isNonRetryable(Throwable cause) {
        if (cause instanceof FindMyMemeException e) {
            return e.getErrorCode() == ErrorCode.REQUEST_INVALID_INPUT ||
                    e.getErrorCode() == ErrorCode.NOT_FOUND_MEME_POST;
        }
        return false;
    }

    private Throwable getRootCause(Throwable throwable) {
        Throwable root = throwable;
        while (root.getCause() != null && root.getCause() != root) {
            root = root.getCause();
        }
        return root;
    }
}
