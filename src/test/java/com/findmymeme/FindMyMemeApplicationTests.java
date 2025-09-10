package com.findmymeme;

import com.findmymeme.memepost.service.ImageCompletionListener;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@SpringBootTest
class FindMyMemeApplicationTests {
	@MockBean
	private S3Client s3Client;

	@MockBean
	private S3Presigner s3Presigner;

	@MockBean
	private LambdaClient lambdaClient;

	@MockBean
	private SqsAsyncClient sqsAsyncClient;

	// SqsAsyncClient에 의존하는 리스너도 Mocking 해주는 것이 안전합니다.
	@MockBean
	private ImageCompletionListener imageCompletionListener;
	@Test
	void contextLoads() {
	}

}
