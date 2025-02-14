package com.findmymeme.file.service;

import com.findmymeme.exception.ErrorCode;
import com.findmymeme.exception.FindMyMemeException;
import com.findmymeme.file.domain.FileMeta;
import com.findmymeme.file.domain.FileType;
import com.findmymeme.file.repository.FileMetaRepository;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class ImageService {

    private static final String IMG_TAG = "img";
    private static final String IMG_SRC = "src";

    private final FileMetaRepository fileMetaRepository;
    private final FileStorageService fileStorageService;
    private final String fileBaseUrl;
    private final String tempDir;
    private final String permanentDir;

    public ImageService(FileStorageService fileStorageService,
                        FileMetaRepository fileMetaRepository,
                        @Value("${file.upload.temp-dir}") String tempDir,
                        @Value("${file.upload.image-dir}") String permanentDir,
                        @Value("${file.base-url}") String fileBaseUrl) {
        this.fileStorageService = fileStorageService;
        this.tempDir = tempDir;
        this.permanentDir = permanentDir;
        this.fileMetaRepository = fileMetaRepository;
        this.fileBaseUrl = fileBaseUrl;
    }

    public List<ImageMeta> convertAndMoveImageUrls(Document doc, FileType fileType) {
        List<ImageMeta> imageMetas = new ArrayList<>();
        doc.select(IMG_TAG).forEach(img -> {
            String tempUrl = convertToRelativeUrl(img.attr(IMG_SRC));
            String permanentUrl = fileStorageService.moveFileToPermanent(tempUrl, fileType);
            img.attr(IMG_SRC, convertToAbsoluteUrl(permanentUrl));
            imageMetas.add(new ImageMeta(findFileMetaByFileUrl(tempUrl), permanentUrl));
        });
        return imageMetas;
    }

    public Set<String> extractImageUrls(Document doc) {
        return doc.select(IMG_TAG)
                .stream()
                .map(img -> img.attr(IMG_SRC))
                .filter(src -> src.startsWith(fileBaseUrl))
                .map(this::convertToRelativeUrl)
                .collect(Collectors.toSet());
    }

    public List<ImageMeta> handleAddedImages(Document doc, Set<String> newImageUrls, FileType fileType) {
        Set<String> addedImageUrls = getAddedImageUrls(newImageUrls);
        replaceImageUrls(doc, addedImageUrls, fileType);
        return getImageMetasFromUrls(addedImageUrls, fileType);
    }

    public Set<String> handleDeletedImages(Set<String> newImageUrls, Set<String> existingImageUrls) {
        Set<String> deletedImageUrls = getDeletedImageUrls(newImageUrls, existingImageUrls);
        deleteImages(deletedImageUrls);
        return deletedImageUrls;
    }

    private List<ImageMeta> getImageMetasFromUrls(Set<String> imageUrls, FileType fileType) {
        return imageUrls.stream()
                .map(this::findFileMetaByFileUrl)
                .map(fileMeta -> new ImageMeta(fileMeta, fileStorageService.convertToPermanentUrl(fileMeta.getFileUrl(), fileType)))
                .collect(Collectors.toList());
    }

    private FileMeta findFileMetaByFileUrl(String fileUrl) {
        return fileMetaRepository.findByFileUrl(fileUrl)
                .orElseThrow(() -> new FindMyMemeException(ErrorCode.NOT_FOUND_FILE_META));
    }

    private Set<String> getAddedImageUrls(Set<String> newImageUrls) {
        return newImageUrls.stream()
                .filter(url -> url.startsWith(tempDir))
                .collect(Collectors.toSet());
    }

    private Set<String> getDeletedImageUrls(Set<String> newImageUrls, Set<String> existingImageUrls) {
        return existingImageUrls.stream()
                .filter(url -> url.startsWith(permanentDir))
                .filter(url -> !newImageUrls.contains(url))
                .collect(Collectors.toSet());
    }

    private void replaceImageUrls(Document doc, Set<String> addedImageUrls, FileType fileType) {
        doc.select(IMG_TAG).forEach(img -> {
            String imgUrl = convertToRelativeUrl(img.attr(IMG_SRC));
            if (addedImageUrls.contains(imgUrl)) {
                String permanentUrl = fileStorageService.moveFileToPermanent(imgUrl, fileType);
                img.attr(IMG_SRC, convertToAbsoluteUrl(permanentUrl));
            }
        });
    }

    private void deleteImages(Set<String> deletedImageUrls) {
        deletedImageUrls.forEach(fileStorageService::deletePermanentFile);
    }

    private String convertToAbsoluteUrl(String permanentUrl) {
        return fileBaseUrl + permanentUrl;
    }

    private String convertToRelativeUrl(String absoluteTempUrl) {
        return absoluteTempUrl.replace(fileBaseUrl, "");
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ImageMeta {
        private FileMeta fileMeta;
        private String imageUrl;

        public ImageMeta(FileMeta fileMeta, String imageUrl) {
            this.fileMeta = fileMeta;
            this.imageUrl = imageUrl;
        }
    }
}
