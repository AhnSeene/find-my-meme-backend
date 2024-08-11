package com.findmymeme.findpost.service;

import com.findmymeme.exception.ErrorCode;
import com.findmymeme.exception.FindMyMemeException;
import com.findmymeme.file.domain.FileMeta;
import com.findmymeme.file.repository.FileMetaRepository;
import com.findmymeme.file.service.FileStorageService;
import com.findmymeme.findpost.domain.FindPost;
import com.findmymeme.findpost.domain.FindPostImage;
import com.findmymeme.findpost.dto.FindPostUploadRequest;
import com.findmymeme.findpost.dto.FindPostUploadResponse;
import com.findmymeme.findpost.repository.FindPostImageRepository;
import com.findmymeme.findpost.repository.FindPostRepository;
import com.findmymeme.user.User;
import com.findmymeme.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FindPostService {

    private static final String IMG_TAG = "img";
    private static final String IMG_SRC = "src";
    private static final String URL_SLASH = "/";

    private final UserRepository userRepository;
    private final FindPostRepository findPostRepository;
    private final FindPostImageRepository findPostImageRepository;
    private final FileMetaRepository fileMetaRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    public FindPostUploadResponse upload(FindPostUploadRequest request, Long userId) {
        User user = findUserById(userId);

        FindPost findPost = createFindPost(request, user);

        return new FindPostUploadResponse(savedFindPost);
    }
    private FindPost createFindPost(FindPostUploadRequest request, User user) {
        return FindPost.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .user(user)
                .build();
    }
    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new FindMyMemeException(ErrorCode.NOT_FOUND_USER));
    }

