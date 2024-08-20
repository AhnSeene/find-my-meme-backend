package com.findmymeme.findpost.service;

import com.findmymeme.exception.ErrorCode;
import com.findmymeme.exception.FindMyMemeException;
import com.findmymeme.file.service.ImageService;
import com.findmymeme.findpost.domain.FindPost;
import com.findmymeme.findpost.domain.FindPostImage;
import com.findmymeme.findpost.dto.*;
import com.findmymeme.findpost.repository.FindPostImageRepository;
import com.findmymeme.findpost.repository.FindPostRepository;
import com.findmymeme.tag.service.PostTagService;
import com.findmymeme.user.domain.User;
import com.findmymeme.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.findmymeme.tag.domain.PostType.*;

@Slf4j
@Service
@Transactional
public class FindPostWriteService {

    private final UserRepository userRepository;
    private final FindPostRepository findPostRepository;
    private final FindPostImageRepository findPostImageRepository;
    private final ImageService imageService;
    private final PostTagService postTagService;

    public FindPostWriteService(
            UserRepository userRepository,
            FindPostRepository findPostRepository,
            FindPostImageRepository findPostImageRepository,
            ImageService imageService,
            PostTagService postTagService
    ) {
        this.userRepository = userRepository;
        this.findPostRepository = findPostRepository;
        this.findPostImageRepository = findPostImageRepository;
        this.imageService = imageService;
        this.postTagService = postTagService;
    }

    public FindPostUploadResponse uploadFindPost(FindPostUploadRequest request, Long userId) {
        User user = getUserById(userId);
        FindPost findPost = createFindPost(request, user);
        Document doc = Jsoup.parse(request.getHtmlContent());
        List<ImageService.ImageMeta> ImageMetas = imageService.convertAndMoveImageUrls(doc);
        List<FindPostImage> findPostImages = createFindPostImages(ImageMetas, findPost);

        findPost.changeHtmlContent(doc.body().html());
        FindPost savedFindPost = findPostRepository.save(findPost);
        findPostImageRepository.saveAll(findPostImages);
        List<String> tags = postTagService.applyTagsToPost(request.getTags(), savedFindPost.getId(), FIND_POST);
        return new FindPostUploadResponse(savedFindPost, tags);
    }

    public FindPostUpdateResponse updateFindPost(FindPostUpdateRequest request, Long findPostId, Long userId) {
        User user = getUserById(userId);
        FindPost findPost = getFindPostById(findPostId);
        verifyOwnership(findPost, user);

        Document doc = Jsoup.parse(request.getHtmlContent());
        Set<String> existingImageUrls = findPostImageRepository.findImageUrlsByFindPost(findPost);
        Set<String> newImageUrls = imageService.extractImageUrls(doc);
        List<ImageService.ImageMeta> addedImageMetas = imageService.handleAddedImages(doc, newImageUrls, existingImageUrls);
        Set<String> deletedImageUrls = imageService.handleDeletedImages(newImageUrls, existingImageUrls);
        updateFindPostImages(findPost, addedImageMetas, deletedImageUrls);

        updateFindPost(findPost, request.getTitle(), request.getContent(), doc.body().html());
        return new FindPostUpdateResponse(findPost);
    }

    public FindPostFoundResponse markFindPostAsFound(Long findPostId, Long userId) {
        FindPost findPost = getFindPostWithUserById(findPostId);
        User user = getUserById(userId);
        verifyOwnership(findPost, user);

        findPost.markAsFound();
        return new FindPostFoundResponse(findPost.getFindStatus());
    }

    private void updateFindPostImages(FindPost findPost, List<ImageService.ImageMeta> addedImageMetas, Set<String> deletedImageUrls) {
        List<FindPostImage> findPostImages = createFindPostImages(addedImageMetas, findPost);
        findPostImageRepository.saveAll(findPostImages);
        findPostImageRepository.deleteByImageUrlIn(deletedImageUrls);
    }


    private FindPost createFindPost(FindPostUploadRequest request, User user) {
        return FindPost.builder()
                .title(request.getTitle())
                .htmlContent(request.getHtmlContent())
                .content(request.getContent())
                .user(user)
                .build();
    }

    private List<FindPostImage> createFindPostImages(Collection<ImageService.ImageMeta> imageMetas, FindPost findPost) {
        return imageMetas.stream()
                .map(image -> {
                    return FindPostImage.builder()
                            .imageUrl(image.getImageUrl())
                            .originalFilename(image.getFileMeta().getOriginalFilename())
                            .findPost(findPost)
                            .build();
                }).toList();
    }


    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new FindMyMemeException(ErrorCode.NOT_FOUND_USER));
    }

    private FindPost getFindPostById(Long findPostId) {
        return findPostRepository.findById(findPostId)
                .orElseThrow(() -> new FindMyMemeException(ErrorCode.NOT_FOUND_FIND_POST));
    }

    private FindPost getFindPostWithUserById(Long findPostId) {
        return findPostRepository.findWithUserById(findPostId)
                .orElseThrow(() -> new FindMyMemeException(ErrorCode.NOT_FOUND_FIND_POST));
    }

    private void verifyOwnership(FindPost findPost, User user) {
        if (!findPost.isOwner(user)) {
            throw new FindMyMemeException(ErrorCode.FORBIDDEN);
        }
    }

    private void updateFindPost(FindPost findPost, String title, String content, String htmlContent) {
        findPost.changeTitle(title);
        findPost.changeContent(content);
        findPost.changeHtmlContent(htmlContent);
    }
}
