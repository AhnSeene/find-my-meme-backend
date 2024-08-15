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
    private static final String URL_SLASH = "/";
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
        String savedFilename = generateStoredFilename(file.getOriginalFilename());
        Path filePath = getTempFilePath(savedFilename);
        try {
            Files.copy(file.getInputStream(), filePath);
        } catch (FileAlreadyExistsException e) {
            throw new FileStorageException(ErrorCode.ALREADY_EXIST_FILE);
        } catch (IOException e) {
            throw new FileStorageException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return getTempFileUrl(savedFilename);
    }

    @Override
    public String moveFileToPermanent(String tempUrl) {
        String savedFilename = getFilename(tempUrl);
        Path tempFilePath = getTempFilePath(savedFilename);
        Path permanentFilePath = getPermanentFilePath(savedFilename);
        try {
            Files.copy(tempFilePath, permanentFilePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (NoSuchFileException e) {
            throw new FileStorageException(ErrorCode.NOT_FOUND_FILE);
        } catch (IOException e) {
            throw new FileStorageException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return getPermanentFileUrl(savedFilename);
    }

    @Override
    public void deleteTempFile(String tempUrl) {
        Path filePath = getTempFilePath(getFilename(tempUrl));
        try {
            Files.deleteIfExists(filePath);
        } catch (NoSuchFileException e) {
            throw new FileStorageException(ErrorCode.NOT_FOUND_FILE);
        } catch (IOException e) {
            throw new FileStorageException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void deletePermanentFile(String permanentUrl) {
        Path filePath = getPermanentFilePath(getFilename(permanentUrl));
        try {
            Files.deleteIfExists(filePath);
        } catch (NoSuchFileException e) {
            throw new FileStorageException(ErrorCode.NOT_FOUND_FILE);
        } catch (IOException e) {
            throw new FileStorageException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
    @Override
    public String convertToPermanentUrl(String tempUrl) {
        return getPermanentFileUrl(getFilename(tempUrl));
    }

    @Override
    public String convertToTempUrl(String permanentUrl) {
        return getTempFileUrl(getFilename(permanentUrl));
    }


    private String getTempFileUrl(String storedFilename) {
        return String.format(URL_FORMAT, tempDir, storedFilename);
    }


    private String getPermanentFileUrl(String storedFilename) {
        return String.format(URL_FORMAT, permanentDir, storedFilename);
    }

    private String getFilename(String url) {
        return url.substring(url.lastIndexOf(URL_SLASH) + 1);
    }

    private Path getTempFilePath(String savedFilename) {
        return Paths.get(baseDir, tempDir, savedFilename);
    }

    private Path getPermanentFilePath(String savedFilename) {
        return Paths.get(baseDir, permanentDir, savedFilename);
    }
}
