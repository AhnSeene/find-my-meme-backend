package com.findmymeme.file.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface FileStorageService {
    String storeTempFile(MultipartFile file);
    void deleteTempFile(String storedFilename);
    String getTempFileUrl(String storedFilename);
    String moveFileToPermanent(String tempFileUrl);
    String getPermanentFileUrl(String storedFilename);
