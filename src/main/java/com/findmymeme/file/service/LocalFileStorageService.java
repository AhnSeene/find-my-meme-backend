package com.findmymeme.file.service;

import com.findmymeme.exception.ErrorCode;
import com.findmymeme.file.domain.FileType;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.BiFunction;
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
    public String storeTempFile(MultipartFile file, Long userId) {
        return storeFile(file, Paths.get(baseDir, tempDir, String.valueOf(userId), generateStoredFilename(file.getOriginalFilename())));
    }

    @Override
    public String storePermanentFile(MultipartFile file, FileType fileType) {
        return storeFile(file, Paths.get(baseDir, permanentDir, fileType.getPrefix(), getYearMonthPath(), generateStoredFilename(file.getOriginalFilename())));
    }

    private String storeFile(MultipartFile file, Path filePath) {
        try {
            Files.createDirectories(filePath.getParent());
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return filePath.toString();
        } catch (IOException e) {
            throw new FileStorageException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public String moveFileToPermanent(String tempFileUrl, FileType fileType) {
        Path tempFilePath = Paths.get(tempFileUrl);
        Path permanentFilePath = Paths.get(baseDir, permanentDir, fileType.getPrefix(), getYearMonthPath(), getFilename(tempFileUrl));

        try {
            Files.move(tempFilePath, permanentFilePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new FileStorageException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        return permanentFilePath.toString();
    }

    @Override
    public Resource downloadFile(String fileUrl) {
        File file = new File(baseDir + URL_SLASH + fileUrl);
        try {
            InputStream inputStream = new FileInputStream(file);
            return new InputStreamResource(inputStream);
        } catch (FileNotFoundException e) {
            throw new FileStorageException(ErrorCode.FILE_DOWNLOAD_ERROR);
        }
    }

    @Override
    public void deleteTempFile(String tempUrl) {
        Path filePath = Paths.get(baseDir, tempUrl);
        deleteFile(filePath);
    }

    @Override
    public void deletePermanentFile(String permanentUrl) {
        Path filePath = Paths.get(baseDir, permanentUrl);
        deleteFile(filePath);
    }


    @Override
    public String convertToPermanentUrl(String tempUrl, FileType fileType) {
        return Paths.get(baseDir, permanentDir, fileType.getPrefix(), getYearMonthPath(), getFilename(tempUrl)).toString();
    }

    @Override
    public String convertToTempUrl(String tempUrl, Long userId) {
        return Paths.get(baseDir, tempDir, String.valueOf(userId), getFilename(tempUrl)).toString();
    }

    private void deleteFile(Path filePath) {
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

    private Path getTempFilePath(String storedFilename, Long userId) {
        return Paths.get(baseDir, tempDir, String.valueOf(userId), storedFilename);
    }

    private Path getPermanentFilePath(String storedFilename, FileType fileType) {
        return Paths.get(baseDir, permanentDir, fileType.getPrefix(), storedFilename);
    }

    private String getYearMonthPath() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
    }
}
