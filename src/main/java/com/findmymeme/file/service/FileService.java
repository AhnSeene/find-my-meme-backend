package com.findmymeme.file.service;

import com.findmymeme.exception.ErrorCode;
import com.findmymeme.exception.FindMyMemeException;
import com.findmymeme.file.domain.FileMeta;
import com.findmymeme.file.repository.FileMetaRepository;
import com.findmymeme.file.dto.FileUploadResponse;
import com.findmymeme.memepost.domain.Resolution;
import com.findmymeme.user.domain.User;
import com.findmymeme.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Service
@Transactional
@RequiredArgsConstructor
public class FileService {

    private final UserRepository userRepository;
    private final FileMetaRepository fileMetaRepository;
    private final FileStorageService fileStorageService;

    public FileUploadResponse uploadFile(MultipartFile file, Long userId) {
        User user = findUserById(userId);
        String tempUrl = null;
        try {
            tempUrl = fileStorageService.storeTempFile(file, userId);
            FileMeta fileMeta = saveFileMeta(file, tempUrl, user);
            return new FileUploadResponse(fileMeta);
        } catch (Exception e) {
            deleteFile(tempUrl);
            throw new FindMyMemeException(ErrorCode.SERVER_ERROR);
        }
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new FindMyMemeException(ErrorCode.NOT_FOUND_USER));
    }

    private FileMeta saveFileMeta(MultipartFile file, String fileUrl, User user) {
        String originalFilename = file.getOriginalFilename();
        FileMeta fileMeta = FileMeta.builder()
                .originalFilename(originalFilename)
                .fileUrl(fileUrl)
                .size(file.getSize())
                .resolution(getResolution(file))
                .extension(fileStorageService.extractExtension(originalFilename))
                .user(user)
                .build();
        return fileMetaRepository.save(fileMeta);
    }

    private void deleteFile(String tempUrl) {
        if (tempUrl != null) {
            fileStorageService.deleteTempFile(tempUrl);
        }
    }

    private Resolution getResolution(MultipartFile file) {
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image != null) {
                int width = image.getWidth();
                int height = image.getHeight();
                return new Resolution(width, height);
            }
        } catch (IOException e) {
            throw new FindMyMemeException(ErrorCode.SERVER_ERROR);
        }
        return new Resolution(0, 0);
    }
}
