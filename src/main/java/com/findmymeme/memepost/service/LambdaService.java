package com.findmymeme.memepost.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.findmymeme.config.AwsLambdaProperties;
import com.findmymeme.memepost.domain.MemePost;
import com.findmymeme.memepost.dto.ImageResizerPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvocationType;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;
import software.amazon.awssdk.services.lambda.model.LambdaException;

@Slf4j
@Service
@RequiredArgsConstructor
public class LambdaService {

    private final LambdaClient lambdaClient;
    private final ObjectMapper objectMapper;
    private final AwsLambdaProperties awsLambdaProperties;

    public void invokeImageResizeLambda(MemePostCreatedEvent event) {
        ImageResizerPayload payload = new ImageResizerPayload(event.getMemePostId(), event.getS3ObjectKey());
        InvokeResponse response;
        try {
            SdkBytes payloadBytes = SdkBytes.fromUtf8String(objectMapper.writeValueAsString(payload));
            InvokeRequest request = InvokeRequest.builder()
                    .functionName(awsLambdaProperties.getFunctionName())
                    .invocationType(InvocationType.EVENT)
                    .payload(payloadBytes)
                    .build();

            response = lambdaClient.invoke(request);
            log.info("성공 = {}", payload.getS3ObjectKey());
            log.info(String.valueOf(response));
        } catch (JsonProcessingException e) {
            log.error("JSON 변환 실패: memePostId={}", payload.getMemePostId(), e);
            throw new RuntimeException("Lambda 페이로드 생성 실패", e);
        } catch (LambdaException e) {
            log.error("Lambda 호출 실패: memePostId={}, errorCode={}",
                    payload.getMemePostId(), e.awsErrorDetails().errorCode(), e);
            throw e;
        }
    }
}
