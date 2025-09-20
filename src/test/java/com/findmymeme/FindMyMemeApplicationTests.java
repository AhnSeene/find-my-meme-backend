package com.findmymeme;

import com.findmymeme.memepost.service.ImageCompletionListener;
import io.awspring.cloud.autoconfigure.s3.S3AutoConfiguration;
import io.awspring.cloud.autoconfigure.sqs.SqsAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@SpringBootTest
@EnableAutoConfiguration(exclude = {
		SqsAutoConfiguration.class,
		S3AutoConfiguration.class
})
class FindMyMemeApplicationTests {
	@MockBean
	private S3Client s3Client;

	@MockBean
	private S3Presigner s3Presigner;

	@MockBean
	private LambdaClient lambdaClient;

	@MockBean
	private SqsAsyncClient sqsAsyncClient;

	@MockBean
	private ImageCompletionListener imageCompletionListener;

	@MockBean
	private RedisMessageListenerContainer redisContainer;
	@Test
	void contextLoads() {
	}

}
