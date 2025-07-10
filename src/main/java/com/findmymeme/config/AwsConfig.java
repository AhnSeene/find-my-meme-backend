package com.findmymeme.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.providers.AwsRegionProvider;
import software.amazon.awssdk.services.lambda.LambdaClient;

@Configuration
public class AwsConfig {

    /**
     * Spring Cloud AWS가 application.yml의 설정으로 자동 생성해주는
     * CredentialsProvider와 RegionProvider를 주입받아서 LambdaClient를 수동으로 생성합니다.
     *
     * @param awsCredentialsProvider 자동 설정된 AWS 자격 증명 공급자
     * @param awsRegionProvider 자동 설정된 AWS 리전 공급자
     * @return 설정이 완료된 LambdaClient 빈
     */
    @Bean
    public LambdaClient lambdaClient(AwsCredentialsProvider awsCredentialsProvider,
                                     AwsRegionProvider awsRegionProvider) {
        return LambdaClient.builder()
                .credentialsProvider(awsCredentialsProvider)
                .region(awsRegionProvider.getRegion())
                .build();
    }
}
