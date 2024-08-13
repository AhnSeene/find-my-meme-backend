package com.findmymeme.file.service;

import com.findmymeme.exception.ErrorCode;
import com.findmymeme.file.exception.FileStorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@Slf4j
@Service
@Transactional
public class LocalFileStorageService implements FileStorageService {

    private static final String URL_FORMAT = "%s/%s";
    private final String tempDir;
    private final String baseDir;
    private final String permanentDir;

    public LocalFileStorageService(
            @Value("${file.upload.temp-dir}") String tempDir,
            @Value("${file.upload.image-dir}") String permanentDir,
            @Value("${file.base-dir}") String baseDir
    ) {
        this.tempDir = tempDir;
        this.permanentDir = permanentDir;
        this.baseDir = baseDir;
    }

    @Override
    public String storeTempFile(MultipartFile file) {
        String storedFilename = generateStoredFilename(file.getOriginalFilename());
        Path filePath = Paths.get(baseDir, tempDir, storedFilename);
        try {
            Files.copy(file.getInputStream(), filePath);
        } catch (FileAlreadyExistsException e) {
            throw new FileStorageException(ErrorCode.ALREADY_EXIST_FILE);
        } catch (IOException e) {
            throw new FileStorageException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return storedFilename;
    }

    @Override
    public void deleteTempFile(String storedFilename) {
        Path filePath = Paths.get(baseDir, tempDir, storedFilename);
        try {
            Files.deleteIfExists(filePath);
        } catch (NoSuchFileException e) {
            throw new FileStorageException(ErrorCode.NOT_FOUND_FILE);
        } catch (IOException e) {
            throw new FileStorageException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public String getTempFileUrl(String storedFilename) {
        return String.format(URL_FORMAT, tempDir, storedFilename);
    }

    @Override
    public String moveFileToPermanent(String storedFilename) {
        Path tempFilePath = Paths.get(baseDir, tempDir, storedFilename);
        Path permanentFilePath = Paths.get(baseDir, permanentDir, storedFilename);
        try {
            Files.copy(tempFilePath, permanentFilePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (NoSuchFileException e) {
            throw new FileStorageException(ErrorCode.NOT_FOUND_FILE);
        } catch (IOException e) {
            throw new FileStorageException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return permanentFilePath.getFileName().toString();
    }

    @Override
    public String getPermanentFileUrl(String storedFilename) {
        return String.format(URL_FORMAT, permanentDir, storedFilename);
    }
}
