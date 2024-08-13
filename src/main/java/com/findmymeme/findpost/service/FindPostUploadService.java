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
public class FindPostUploadService {

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

        Document doc = Jsoup.parse(request.getHtmlContent());
        List<FindPostImage> findPostImages = processImages(doc, findPost);

        findPost.changeContent(doc.body().html());
        FindPost savedFindPost = findPostRepository.save(findPost);
        findPostImageRepository.saveAll(findPostImages);

        return new FindPostUploadResponse(savedFindPost);
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
            String tempUrl = img.attr(IMG_SRC);
            String storedFilename = extractFilenameFromUrl(tempUrl);
            String permanentFilename = fileStorageService.moveFileToPermanent(storedFilename);
            String permanentUrl = fileStorageService.getPermanentFileUrl(permanentFilename);

            img.attr(IMG_SRC, permanentUrl);

            FileMeta fileMeta = findFileMetaByFileUrl(tempUrl);
            FindPostImage findPostImage = createFindPostImage(findPost, permanentUrl, fileMeta);
            findPostImages.add(findPostImage);
        });
        return findPostImages;
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

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new FindMyMemeException(ErrorCode.NOT_FOUND_USER));
    }


    private String extractFilenameFromUrl(String url) {
        return url.substring(url.lastIndexOf(URL_SLASH) + 1);
    }
}
