package com.findmymeme.file.service;

import com.findmymeme.exception.ErrorCode;
import com.findmymeme.exception.FindMyMemeException;
import com.findmymeme.file.domain.FileMeta;
import com.findmymeme.file.dto.FileMetaRequest;
import com.findmymeme.file.dto.FileUploadResponse;
import com.findmymeme.file.dto.PresignedUploadResponse;
import com.findmymeme.file.repository.FileMetaRepository;
import com.findmymeme.memepost.domain.Resolution;
import com.findmymeme.user.domain.User;
import com.findmymeme.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

@Slf4j
@Service
public class S3PresignedUrlService {

    private final S3Presigner s3Presigner;
    private final FileStorageService fileStorageService;
    private final UserRepository userRepository;
    private final FileMetaRepository fileMetaRepository;
    private final String bucket;
    private final String tempDir;
    private final int presignedDuration;
    private static final String URL_FORMAT = "%s/%d/%s"; // temps/{userId}/{UUID.ext}

    public S3PresignedUrlService(
            S3Presigner s3Presigner,
            FileStorageService fileStorageService,
            UserRepository userRepository,
            FileMetaRepository fileMetaRepository,
            @Value("${file.upload.temp-dir}") String tempDir,
            @Value("${aws.s3.presigned-duration}") int presignedDuration,
            @Value("${aws.s3.bucket}") String bucket
    ) {
        this.s3Presigner = s3Presigner;
        this.fileStorageService = fileStorageService;
        this.userRepository = userRepository;
        this.fileMetaRepository = fileMetaRepository;
        this.bucket = bucket;
        this.tempDir = tempDir;
        this.presignedDuration = presignedDuration;
    }

    public PresignedUploadResponse generatePresignedUrl(String originalFilename, Long userId) {
        String key = generateKey(tempDir, userId, fileStorageService.generateStoredFilename(originalFilename));
        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(req -> req
                .signatureDuration(Duration.ofMinutes(presignedDuration))
                .putObjectRequest(p ->
                        p.bucket(bucket)
                                .key(key)));

        return new PresignedUploadResponse(presignedRequest.url().toString());
    }

    public FileUploadResponse saveFileMeta(FileMetaRequest request, Long userId) {
        User user = getUserById(userId);

        FileMeta fileMeta = FileMeta.builder()
                .originalFilename(request.getOriginalFilename())
                .fileUrl(extractFileKey(request.getPresignedUrl()))
                .size(request.getSize())
                .resolution(determineResolution(request))
                .extension(fileStorageService.extractExtension(request.getOriginalFilename()))
                .user(user)
                .build();
        return new FileUploadResponse(fileMetaRepository.save(fileMeta));
    }

    public String generatePresignedDownloadUrl(String objectKey) {
        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(req -> req
                .signatureDuration(Duration.ofMinutes(presignedDuration))
                .getObjectRequest(p -> p.bucket(bucket).key(objectKey)));

        return presignedRequest.url().toString();
    }

    private String generateKey(String dir, Long userId, String filename) {
        return String.format(URL_FORMAT, dir, userId, filename);
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new FindMyMemeException(ErrorCode.NOT_FOUND_USER));
    }

    private String extractFileKey(String presignedUrl) {
        try {
            URL url = new URL(presignedUrl);
            String path = url.getPath();
            return path.substring(1);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid Presigned URL", e);
        }
    }

    private Resolution determineResolution(FileMetaRequest request) {
        int width = (request.getWidth() != null) ? request.getWidth() : 0;
        int height = (request.getHeight() != null) ? request.getHeight() : 0;

        return new Resolution(width, height);
    }


}
