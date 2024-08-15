package com.findmymeme.file.service;

import com.findmymeme.exception.ErrorCode;
import com.findmymeme.exception.FindMyMemeException;
import com.findmymeme.file.domain.FileMeta;
import com.findmymeme.file.repository.FileMetaRepository;
import com.findmymeme.file.dto.FileUploadResponse;
import com.findmymeme.user.User;
import com.findmymeme.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
            tempUrl = fileStorageService.storeTempFile(file);
            FileMeta fileMeta = saveFileMeta(file, tempUrl, user);
            return new FileUploadResponse(fileMeta);
        } catch (Exception e) {
            deleteFile(tempUrl);
            throw new FindMyMemeException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new FindMyMemeException(ErrorCode.NOT_FOUND_USER));
    }

    private FileMeta saveFileMeta(MultipartFile file, String fileUrl, User user) {
        FileMeta fileMeta = FileMeta.builder()
                .originalFilename(file.getOriginalFilename())
                .fileUrl(fileUrl)
                .user(user)
                .build();
        return fileMetaRepository.save(fileMeta);
    }

    private void deleteFile(String tempUrl) {
        if (tempUrl != null) {
            fileStorageService.deleteTempFile(tempUrl);
        }
    }
}
