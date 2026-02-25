package com.findmymeme.memepost.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.findmymeme.memepost.dto.ImageResizerPayload;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageProcessingService {

    private final SqsTemplate sqsTemplate;
    private final ObjectMapper objectMapper;

    @Value("${aws.sqs.image-input-queue}")
    private String imageInputQueueUrl;

    public void sendImageProcessingRequest(MemePostCreatedEvent event) {
        ImageResizerPayload payload = new ImageResizerPayload(
                event.getMemePostId(),
                event.getUserId(),
                event.getS3ObjectKey()
        );

        try {
            String messageBody = objectMapper.writeValueAsString(payload);
            sqsTemplate.send(to -> to
                    .queue(imageInputQueueUrl)
                    .payload(messageBody)
            );
            log.info("SQS 메시지 전송 성공: memePostId={}, s3Key={}", payload.getMemePostId(), payload.getS3ObjectKey());
        } catch (JsonProcessingException e) {
            log.error("JSON 변환 실패: memePostId={}", payload.getMemePostId(), e);
            throw new RuntimeException("SQS 메시지 생성 실패", e);
        } catch (Exception e) {
            log.error("SQS 메시지 전송 실패: memePostId={}", payload.getMemePostId(), e);
            throw e;
        }
    }
}