package com.findmymeme.memepost.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.findmymeme.config.AwsLambdaProperties;
import com.findmymeme.memepost.dto.ImageResizerPayload;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvocationType;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LambdaServiceTest {
    @InjectMocks
    private LambdaService lambdaService;

    @Mock
    private LambdaClient lambdaClient;
    @Mock private ObjectMapper objectMapper;
    @Mock private AwsLambdaProperties awsLambdaProperties;
    @Test
    void invokeImageResizeLambda_Success() throws JsonProcessingException {
        // given
        MemePostCreatedEvent event = new MemePostCreatedEvent(589820L, 1L, "images/memes/test.jpg");
        String functionName = "ResizeImage-prod";
        String payloadJson = "{\"memePostId\":589820,\"s3ObjectKey\":\"images/memes/test.jpg\"}";

        given(awsLambdaProperties.getFunctionName()).willReturn(functionName);
        given(objectMapper.writeValueAsString(any(ImageResizerPayload.class))).willReturn(payloadJson);

        ArgumentCaptor<InvokeRequest> requestCaptor = ArgumentCaptor.forClass(InvokeRequest.class);

        // when
        lambdaService.invokeImageResizeLambda(event);

        // then
        verify(lambdaClient, times(1)).invoke(requestCaptor.capture());
        InvokeRequest capturedRequest = requestCaptor.getValue();

        assertThat(capturedRequest.functionName()).isEqualTo(functionName);
        assertThat(capturedRequest.invocationType()).isEqualTo(InvocationType.EVENT);
        assertThat(capturedRequest.payload().asUtf8String()).isEqualTo(payloadJson);
    }
}