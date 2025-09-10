package com.findmymeme.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.providers.AwsRegionProvider;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.LambdaClientBuilder;

import java.net.URI;
@Profile({"local", "prod"})
@Configuration
public class AwsConfig {

    @Value("${spring.cloud.aws.endpoint:#{null}}")
    private String endpoint;

    /**
     * Spring Cloud AWS가 application.yml의 설정으로 자동 생성해주는
     * CredentialsProvider와 RegionProvider를 주입받아서 LambdaClient를 수동으로 생성합니다.
     * 로컬 환경일 경우, 주입받은 endpoint로 설정을 오버라이드합니다.
     *
     * @param awsCredentialsProvider 자동 설정된 AWS 자격 증명 공급자
     * @param awsRegionProvider 자동 설정된 AWS 리전 공급자
     * @return 설정이 완료된 LambdaClient 빈
     */
    @Bean
    public LambdaClient lambdaClient(AwsCredentialsProvider awsCredentialsProvider,
                                     AwsRegionProvider awsRegionProvider) {
        LambdaClientBuilder builder = LambdaClient.builder()
                .credentialsProvider(awsCredentialsProvider)
                .region(awsRegionProvider.getRegion());

        if (endpoint != null) {
            builder.endpointOverride(URI.create(endpoint));
        }

        return builder.build();

    }
}
