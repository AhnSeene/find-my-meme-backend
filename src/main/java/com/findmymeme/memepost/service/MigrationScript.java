package com.findmymeme.memepost.service;

import com.findmymeme.memepost.domain.MemePost;
import com.findmymeme.memepost.repository.MemePostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvocationType;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
/**
 * 데이터 마이그레이션을 위한 독립 실행 스크립트.
 * 'migration' 프로필이 활성화될 때만 실행됩니다.
 */
@Slf4j
@Component
@Profile("migration")
@RequiredArgsConstructor
public class MigrationScript implements CommandLineRunner {

    @Value("${aws.lambda.function-name}")
    private String lambdaFunctionName;

    private static final int BATCH_SIZE = 100;
    private static final long DELAY_BETWEEN_BATCHES_MS = 1000;
    private final MemePostRepository memePostRepository;
    private final LambdaClient lambdaClient;

    @Override
    public void run(String... args) throws Exception {
        log.info("==================================================");
        log.info("데이터 마이그레이션 스크립트를 시작합니다.");
        log.info("==================================================");

        int currentPage = 0;
        long totalProcessedCount = 0;

        while (true) {
            Page<MemePost> postPage = memePostRepository.findAll(PageRequest.of(currentPage, BATCH_SIZE));

            if (!postPage.hasContent()) {
                log.info("처리할 데이터가 더 이상 없습니다.");
                break;
            }

            log.info("페이지 {}의 데이터 {}개를 처리합니다.", currentPage, postPage.getNumberOfElements());

            for (MemePost post : postPage.getContent()) {
                // 4. Lambda를 호출할 페이로드를 생성합니다.
                String payload = String.format(
                        "{\"memePostId\": %d, \"userId\": %d, \"s3ObjectKey\": \"%s\", \"invocationSource\": \"MIGRATION_SCRIPT\"}",
                        post.getId(), post.getUser().getId(), post.getImageUrl()
                );

                InvokeRequest request = InvokeRequest.builder()
                        .functionName(lambdaFunctionName)
                        .invocationType(InvocationType.EVENT)
                        .payload(SdkBytes.fromUtf8String(payload))
                        .build();

                lambdaClient.invoke(request);
            }

            totalProcessedCount += postPage.getNumberOfElements();
            log.info("총 {}개 게시물 처리 요청 완료. 다음 배치를 위해 잠시 대기합니다...", totalProcessedCount);

            currentPage++;
            Thread.sleep(DELAY_BETWEEN_BATCHES_MS);
        }

        log.info("==================================================");
        log.info("모든 마이그레이션 작업 요청이 완료되었습니다.");
        log.info("==================================================");
    }
}