package com.findmymeme.file.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface FileStorageService {
    String storeTempFile(MultipartFile file);
    String storePermanentFile(MultipartFile file);
    void deleteTempFile(String tempUrl);
    void deletePermanentFile(String permanentUrl);
    String moveFileToPermanent(String tempFileUrl);
    Resource downloadFile(String fileUrl);
    String convertToPermanentUrl(String permanentUrl);
    String convertToTempUrl(String tempUrl);
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
