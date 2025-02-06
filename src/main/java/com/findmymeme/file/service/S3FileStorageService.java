package com.findmymeme.file.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.findmymeme.exception.ErrorCode;
import com.findmymeme.file.exception.FileStorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
@Profile("prod")
public class S3FileStorageService implements FileStorageService {
    private static final String URL_FORMAT = "%s/%s";
    private static final String URL_SLASH = "/";
    private final AmazonS3 amazonS3;

    private final String tempDir;
    private final String permanentDir;
    private final String bucket;

    public S3FileStorageService(
            AmazonS3 amazonS3,
            @Value("${file.upload.temp-dir}") String tempDir,
            @Value("${file.upload.image-dir}") String permanentDir,
            @Value("${cloud.aws.s3.bucket}") String bucket
    ) {
        this.amazonS3 = amazonS3;
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

        CopyObjectRequest copyObjectRequest = new CopyObjectRequest(this.bucket, orgKey, this.bucket, copyKey);
        copyObjectRequest.setCannedAccessControlList(CannedAccessControlList.PublicRead);
        amazonS3.copyObject(copyObjectRequest);

        return copyKey;
    }

    @Override
    public Resource downloadFile(String fileUrl) {
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucket, fileUrl);
        return new InputStreamResource(amazonS3.getObject(getObjectRequest).getObjectContent());
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
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            PutObjectRequest putObjectRequest = new PutObjectRequest(this.bucket, key, file.getInputStream(), metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead);
            amazonS3.putObject(putObjectRequest);
            return key;
        } catch (AmazonS3Exception | IOException e) {
            throw new FileStorageException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private void deleteFile(String tempUrl) {
        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(this.bucket, tempUrl);
        amazonS3.deleteObject(deleteObjectRequest);
    }

    private String generateKey(String dir, String filename) {
        return String.format(URL_FORMAT, dir, filename);
    }

}
