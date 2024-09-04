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

import java.io.*;
import java.nio.file.*;
import java.util.function.Function;

@Slf4j
@Service
@Profile("local")
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
        return storeFile(file, this::getTempFilePath, this::getTempFileUrl);
    }

    @Override
    public String storePermanentFile(MultipartFile file) {
        return storeFile(file, this::getPermanentFilePath, this::getPermanentFileUrl);
    }

    @Override
    public String moveFileToPermanent(String tempUrl) {
        String savedFilename = getFilename(tempUrl);
        Path tempFilePath = getTempFilePath(savedFilename);
        Path permanentFilePath = getPermanentFilePath(savedFilename);
        try {
            log.info(savedFilename);
            log.info(tempFilePath.toString());
            log.info(permanentFilePath.toString());
            Files.copy(tempFilePath, permanentFilePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (NoSuchFileException e) {
            throw new FileStorageException(ErrorCode.NOT_FOUND_FILE);
        } catch (IOException e) {
            throw new FileStorageException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return getPermanentFileUrl(savedFilename);
    }

    @Override
    public Resource downloadFile(String fileUrl) {
        File file = new File(baseDir + URL_SLASH + fileUrl);
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new FileStorageException(ErrorCode.FILE_DOWNLOAD_ERROR);
        }
        return new InputStreamResource(inputStream);
    }

    @Override
    public void deleteTempFile(String tempUrl) {
        deleteFile(tempUrl, this::getTempFilePath);
    }

    @Override
    public void deletePermanentFile(String permanentUrl) {
        deleteFile(permanentUrl, this::getPermanentFilePath);
    }

    @Override
    public String convertToPermanentUrl(String tempUrl) {
        return getPermanentFileUrl(getFilename(tempUrl));
    }

    @Override
    public String convertToTempUrl(String permanentUrl) {
        return getTempFileUrl(getFilename(permanentUrl));
    }

    private String storeFile(MultipartFile file,
                             Function<String, Path> pathFunction,
                             Function<String, String> urlFunction) {
        String savedFilename = generateStoredFilename(file.getOriginalFilename());
        Path filePath = pathFunction.apply(savedFilename);
        try {
            Files.copy(file.getInputStream(), filePath);
        } catch (FileAlreadyExistsException e) {
            throw new FileStorageException(ErrorCode.ALREADY_EXIST_FILE);
        } catch (IOException e) {
            throw new FileStorageException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return urlFunction.apply(savedFilename);
    }

    private void deleteFile(String fileUrl, Function<String, Path> pathFunction) {
        Path filePath = pathFunction.apply(getFilename(fileUrl));
        try {
            Files.deleteIfExists(filePath);
        } catch (NoSuchFileException e) {
            throw new FileStorageException(ErrorCode.NOT_FOUND_FILE);
        } catch (IOException e) {
            throw new FileStorageException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private String getTempFileUrl(String storedFilename) {
        return String.format(URL_FORMAT, tempDir, storedFilename);
    }


    private String getPermanentFileUrl(String storedFilename) {
        return String.format(URL_FORMAT, permanentDir, storedFilename);
    }

    private Path getTempFilePath(String savedFilename) {
        return Paths.get(baseDir, tempDir, savedFilename);
    }

    private Path getPermanentFilePath(String savedFilename) {
        return Paths.get(baseDir, permanentDir, savedFilename);
    }
}
