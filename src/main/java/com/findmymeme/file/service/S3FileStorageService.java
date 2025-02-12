package com.findmymeme.file.service;

import com.findmymeme.exception.ErrorCode;
import com.findmymeme.file.exception.FileStorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
@Profile("prod")
public class S3FileStorageService implements FileStorageService {
    private static final String URL_FORMAT = "%s/%s";
    private static final String URL_SLASH = "/";

    private final S3Client s3Client;
    private final String tempDir;
    private final String permanentDir;
    private final String bucket;

    public S3FileStorageService(
            S3Client s3Client,
            @Value("${file.upload.temp-dir}") String tempDir,
            @Value("${file.upload.image-dir}") String permanentDir,
            @Value("${aws.s3.bucket}") String bucket
    ) {
        this.s3Client = s3Client;
        this.tempDir = tempDir;
        this.permanentDir = permanentDir;
        this.bucket = bucket;
    }

    @Override
    public String storeTempFile(MultipartFile file) {
        return storeFile(file, tempDir);
    }

    @Override
    public String storePermanentFile(MultipartFile file) {
        return storeFile(file, permanentDir);
    }

    @Override
    public void deleteTempFile(String tempUrl) {
        deleteFile(tempUrl);
    }

    @Override
    public void deletePermanentFile(String permanentUrl) {
        deleteFile(permanentUrl);
    }

    @Override
    public String moveFileToPermanent(String tempFileUrl) {
        String orgKey = tempDir + URL_SLASH + getFilename(tempFileUrl);
        String copyKey = permanentDir + URL_SLASH + getFilename(tempFileUrl);

        CopyObjectRequest copyObjectRequest = CopyObjectRequest.builder()
                .sourceBucket(bucket)
                .sourceKey(orgKey)
                .destinationBucket(bucket)
                .destinationKey(copyKey)
                .build();

        s3Client.copyObject(copyObjectRequest);

        return copyKey;
    }

    @Override
    public Resource downloadFile(String fileUrl) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(fileUrl)
                .build();
        InputStream inputStream = s3Client.getObject(getObjectRequest);
        return new InputStreamResource(inputStream);
    }

    @Override
    public String convertToPermanentUrl(String permanentUrl) {
        return generateKey(permanentDir, getFilename(permanentUrl));
    }

    @Override
    public String convertToTempUrl(String tempUrl) {
        return generateKey(tempDir, getFilename(tempUrl));
    }

    private String storeFile(MultipartFile file, String dir) {
        String key = generateKey(dir, generateStoredFilename(file.getOriginalFilename()));
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .acl(ObjectCannedACL.PUBLIC_READ)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            return key;
        } catch (IOException e) {
            throw new FileStorageException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private void deleteFile(String fileUrl) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(fileUrl)
                .build();
        s3Client.deleteObject(deleteObjectRequest);
    }

    private String generateKey(String dir, String filename) {
        return String.format(URL_FORMAT, dir, filename);
    }

}
