package com.findmymeme.findpost.service;

import com.findmymeme.exception.ErrorCode;
import com.findmymeme.exception.FindMyMemeException;
import com.findmymeme.file.domain.FileMeta;
import com.findmymeme.file.repository.FileMetaRepository;
import com.findmymeme.file.service.FileStorageService;
import com.findmymeme.findpost.domain.FindPost;
import com.findmymeme.findpost.domain.FindPostImage;
import com.findmymeme.findpost.dto.FindPostUpdateRequest;
import com.findmymeme.findpost.dto.FindPostUpdateResponse;
import com.findmymeme.findpost.dto.FindPostUploadRequest;
import com.findmymeme.findpost.dto.FindPostUploadResponse;
import com.findmymeme.findpost.repository.FindPostImageRepository;
import com.findmymeme.findpost.repository.FindPostRepository;
import com.findmymeme.user.User;
import com.findmymeme.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class FindPostWriteService {

    private static final String IMG_TAG = "img";
    private static final String IMG_SRC = "src";
    private static final String URL_SLASH = "/";

    private final UserRepository userRepository;
    private final FindPostRepository findPostRepository;
    private final FindPostImageRepository findPostImageRepository;
    private final FileMetaRepository fileMetaRepository;
    private final FileStorageService fileStorageService;
    private final String serverBaseUrl;

    public FindPostWriteService(
            UserRepository userRepository,
            FindPostRepository findPostRepository,
            FindPostImageRepository findPostImageRepository,
            FileMetaRepository fileMetaRepository,
            FileStorageService fileStorageService,
            @Value("${server.base-url}") String serverBaseUrl
    ) {
        this.userRepository = userRepository;
        this.findPostRepository = findPostRepository;
        this.findPostImageRepository = findPostImageRepository;
        this.fileMetaRepository = fileMetaRepository;
        this.fileStorageService = fileStorageService;
        this.serverBaseUrl = serverBaseUrl;
    }

    public FindPostUploadResponse uploadFindPost(FindPostUploadRequest request, Long userId) {
        User user = getUserById(userId);

        FindPost findPost = createFindPost(request, user);
        Document doc = Jsoup.parse(request.getHtmlContent());
        List<FindPostImage> findPostImages = processImages(doc, findPost);

        findPost.changeHtmlContent(doc.body().html());
        FindPost savedFindPost = findPostRepository.save(findPost);
        findPostImageRepository.saveAll(findPostImages);

        return new FindPostUploadResponse(savedFindPost);
    }

    public FindPostUpdateResponse updateFindPost(FindPostUpdateRequest request, Long findPostId, Long userId) {
        User user = getUserById(userId);
        FindPost findPost = getFindPostById(findPostId);

        if (isNotOwner(findPost, user)) {
            throw new FindMyMemeException(ErrorCode.FORBIDDEN);
        }

        Document doc = Jsoup.parse(request.getHtmlContent());
        Set<String> newImageUrls = extractImageUrls(doc);

        processAndReplaceImageUrls(doc, newImageUrls, findPost);
        updateFindPost(findPost, request.getTitle(), request.getContent(), doc.body().html());
        return new FindPostUpdateResponse(findPost);
    }

    private Set<String> extractImageUrls(Document doc) {
        return doc.select(IMG_TAG)
                .stream()
                .map(img -> img.attr(IMG_SRC))
                .filter(src -> !src.isEmpty())
                .map(this::convertToRelativeUrl)
                .map(this::convertToPermanentUrl)
                .collect(Collectors.toSet());
    }

    private void processAndReplaceImageUrls(Document doc, Set<String> newImageUrls, FindPost findPost) {
        Set<String> existingImageUrls = findPostImageRepository.findImageUrlsByFindPost(findPost);

        Set<String> addedImageUrls = getAddedImageUrls(newImageUrls, existingImageUrls);
        Set<String> deletedImageUrls = getDeletedImageUrls(newImageUrls, existingImageUrls);

        List<FindPostImage> findPostImages = createFindPostImages(doc, addedImageUrls, findPost);

        findPostImageRepository.saveAll(findPostImages);
        findPostImageRepository.deleteByImageUrlIn(deletedImageUrls);
    }

    private Set<String> getAddedImageUrls(Set<String> newImageUrls, Set<String> existingImageUrls) {
        return newImageUrls.stream()
                .filter(url -> !existingImageUrls.contains(url))
                .map(this::convertToTempUrl)
                .collect(Collectors.toSet());
    }

    private Set<String> getDeletedImageUrls(Set<String> newImageUrls, Set<String> existingImageUrls) {
        return existingImageUrls.stream()
                .filter(url -> !newImageUrls.contains(url))
                .collect(Collectors.toSet());
    }

    private List<FindPostImage> createFindPostImages(Document doc, Set<String> addedImageUrls, FindPost findPost) {
        return doc.select(IMG_TAG)
                .stream()
                .map(img -> {
                    String imgUrl = convertToRelativeUrl(img.attr(IMG_SRC));
                    if (addedImageUrls.contains(imgUrl)) {
                        String permanentFilename = fileStorageService.moveFileToPermanent(getFilename(imgUrl));
                        String permanentUrl = fileStorageService.getPermanentFileUrl(permanentFilename);

                        String originalFilename = findFileMetaByFileUrl(imgUrl).getOriginalFilename();

                        img.attr(IMG_SRC, convertToAbsoluteUrl(permanentUrl));

                        return FindPostImage.builder()
                                .imageUrl(permanentUrl)
                                .originalFilename(originalFilename)
                                .findPost(findPost)
                                .build();
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .toList();
    }


    /**
     * 주어진 문서에서 이미지를 처리합니다:
     * <ul>
     *     <li>임시 이미지 URL을 영구 이미지 URL로 변경합니다.</li>
     *     <li>파일을 임시 저장소에서 영구 저장소로 이동합니다.</li>
     *     <li>처리된 이미지에 대한 FindPostImage 엔티티를 생성합니다.</li>
     * </ul>
     *
     * @param doc      이미지 태그가 포함된 콘텐츠를 담고 있는 Document 객체
     * @param findPost 이미지와 연관될 FindPost 엔티티
     * @return 처리된 이미지를 위한 FindPostImage 엔티티의 리스트
     */
    private List<FindPostImage> processImages(Document doc, FindPost findPost) {
        List<FindPostImage> findPostImages = new ArrayList<>();
        doc.select(IMG_TAG).forEach(img -> {
            String tempUrl = convertToRelativeUrl(img.attr(IMG_SRC));
            String storedFilename = getFilename(tempUrl);
            String permanentFilename = fileStorageService.moveFileToPermanent(storedFilename);
            String permanentUrl = fileStorageService.getPermanentFileUrl(permanentFilename);

            img.attr(IMG_SRC, convertToAbsoluteUrl(permanentUrl));

            FileMeta fileMeta = findFileMetaByFileUrl(tempUrl);
            FindPostImage findPostImage = createFindPostImage(findPost, permanentUrl, fileMeta);
            findPostImages.add(findPostImage);
        });
        return findPostImages;
    }

    private String convertToTempUrl(String permanentUrl) {
        return fileStorageService.getTempFileUrl(getFilename(permanentUrl));
    }

    private String convertToPermanentUrl(String tempUrl) {
        return fileStorageService.getPermanentFileUrl(getFilename(tempUrl));
    }

    private String convertToAbsoluteUrl(String permanentUrl) {
        return serverBaseUrl + permanentUrl;
    }

    private String convertToRelativeUrl(String absoluteTempUrl) {
        return absoluteTempUrl.replace(serverBaseUrl, "");
    }

    private FindPost createFindPost(FindPostUploadRequest request, User user) {
        return FindPost.builder()
                .title(request.getTitle())
                .htmlContent(request.getHtmlContent())
                .content(request.getContent())
                .user(user)
                .build();
    }

    private FindPostImage createFindPostImage(FindPost findPost, String permanentUrl, FileMeta fileMeta) {
        return FindPostImage.builder()
                .imageUrl(permanentUrl)
                .originalFilename(fileMeta.getOriginalFilename())
                .findPost(findPost)
                .build();
    }

    private FileMeta findFileMetaByFileUrl(String tempUrl) {
        return fileMetaRepository.findByFileUrl(tempUrl)
                .orElseThrow(() -> new FindMyMemeException(ErrorCode.NOT_FOUND_FILE_META));
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new FindMyMemeException(ErrorCode.NOT_FOUND_USER));
    }


    private String getFilename(String url) {
        return url.substring(url.lastIndexOf(URL_SLASH) + 1);
    }

    private FindPost getFindPostById(Long findPostId) {
        return findPostRepository.findById(findPostId)
                .orElseThrow(() -> new FindMyMemeException(ErrorCode.NOT_FOUND_FIND_POST));
    }

    private boolean isNotOwner(FindPost findPost, User user) {
        return !findPost.isOwner(user);
    }

    private void updateFindPost(FindPost findPost, String title, String content, String htmlContent) {
        findPost.changeTitle(title);
        findPost.changeContent(content);
        findPost.changeHtmlContent(htmlContent);
    }
}
