package com.findmymeme.file.service;

import com.findmymeme.file.domain.FileType;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface FileStorageService {
    String storeTempFile(MultipartFile file, Long userId);
    String storePermanentFile(MultipartFile file, FileType fileType);
    void deleteTempFile(String tempUrl);
    void deletePermanentFile(String permanentUrl);
    String moveFileToPermanent(String tempFileUrl, FileType fileType);
    Resource downloadFile(String fileUrl);
    String generatePresignedDownloadUrl(String objectKey);
    String convertToPermanentUrl(String permanentUrl, FileType fileType);
    String convertToTempUrl(String tempUrl, Long userId);
    default String generateStoredFilename(String originalFilename) {
        String ext = extractExtension(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    default String extractExtension(String originalFilename) {
        return originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
    }

    default String getFilename(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }
}
