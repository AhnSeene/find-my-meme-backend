package com.findmymeme.findpost.service;

import com.findmymeme.exception.ErrorCode;
import com.findmymeme.exception.FindMyMemeException;
import com.findmymeme.file.service.ImageService;
import com.findmymeme.findpost.domain.FindPost;
import com.findmymeme.findpost.domain.FindPostComment;
import com.findmymeme.findpost.domain.FindPostCommentImage;
import com.findmymeme.findpost.dto.*;
import com.findmymeme.findpost.repository.FindPostCommentImageRepository;
import com.findmymeme.findpost.repository.FindPostCommentRepository;
import com.findmymeme.findpost.repository.FindPostRepository;
import com.findmymeme.user.domain.User;
import com.findmymeme.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FindPostCommentWriteService {

    private final UserRepository userRepository;
    private final FindPostRepository findPostRepository;
    private final FindPostCommentRepository commentRepository;
    private final FindPostCommentImageRepository commentImageRepository;
    private final ImageService imageService;


    public FindPostCommentAddResponse addComment(FindPostCommentAddRequest request, Long postId, Long userId) {
        User user = getUserById(userId);
        FindPost findPost = getFindPostById(postId);
        validateFindPostStatus(findPost);

        FindPostComment comment = createFindPostComment(request, findPost, user);
        Document doc = Jsoup.parse(request.getHtmlContent());
        List<ImageService.ImageMeta> imageMetas = imageService.convertAndMoveImageUrls(doc);
        List<FindPostCommentImage> commentImages = createCommentImages(imageMetas, comment);

        comment.changeHtmlContent(doc.body().html());
        FindPostComment savedComment = commentRepository.save(comment);
        commentImageRepository.saveAll(commentImages);
        findPost.incrementCommentCount();
        return new FindPostCommentAddResponse(savedComment);
    }

    public FindPostCommentUpdateResponse updateComment(FindPostCommentUpdateRequest request, Long findPostId, Long commentId, Long userId) {
        FindPost findPost = getFindPostById(findPostId);
        validateFindPostStatus(findPost);

        FindPostComment comment = getCommentWithUserById(commentId);
        validateFindPost(comment, findPostId);
        verifyOwnership(comment, userId);

        Document doc = Jsoup.parse(request.getHtmlContent());
        Set<String> existingImageUrls = commentImageRepository.findImageUrlsByComment(comment);
        Set<String> newImageUrls = imageService.extractImageUrls(doc);
        List<ImageService.ImageMeta> addedImageMetas = imageService.handleAddedImages(doc, newImageUrls, existingImageUrls);
        Set<String> deletedImageUrls = imageService.handleDeletedImages(newImageUrls, existingImageUrls);
        updateCommentImages(comment, addedImageMetas, deletedImageUrls);

        updateFindPostComment(comment, request.getContent(), doc.body().html());
        return new FindPostCommentUpdateResponse(comment);
    }

    public FindPostCommentDeleteResponse softDelete(Long findPostId, Long commentId, Long userId) {
        FindPostComment comment = getCommentWithUserById(commentId);
        validateAlreadyDeleted(comment);
        validateFindPost(comment, findPostId);
        verifyOwnership(comment, userId);
        comment.softDelete();
        return new FindPostCommentDeleteResponse(comment, comment.isAuthor(userId));
    }


    private void validateAlreadyDeleted(FindPostComment comment) {
        if (comment.isDeleted()) {
            throw new FindMyMemeException(ErrorCode.ALREADY_DELETED_COMMENT);
        }
    }

    private void validateFindPostStatus(FindPost findPost) {
        if (findPost.isDeleted()) {
            throw new FindMyMemeException(ErrorCode.CANNOT_WRITE_COMMENT_ON_DELETED_POST);
        }
    }

    private void validateFindPost(FindPostComment comment, Long findPostId) {
        if (comment.isNotOfPost(findPostId)) {
            throw new FindMyMemeException(ErrorCode.COMMENT_NOT_BELONG_TO_POST);
        }
    }


    private FindPostComment getCommentWithUserById(Long commentId) {
        return commentRepository.findWithUserById(commentId)
                .orElseThrow(() -> new FindMyMemeException(ErrorCode.NOT_FOUND_FIND_POST_COMMENT));
    }


    private FindPostComment createFindPostComment(FindPostCommentAddRequest request, FindPost findPost, User user) {
        FindPostComment parentComment = null;
        if (request.getParentCommentId() != null) {
            parentComment = getParentCommentById(request.getParentCommentId());
        }
        return FindPostComment.builder()
                .htmlContent(request.getHtmlContent())
                .content(request.getContent())
                .parentComment(parentComment)
                .findPost(findPost)
                .user(user)
                .build();
    }

    private FindPostComment getParentCommentById(Long parentCommentId) {
        return commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new FindMyMemeException(ErrorCode.NOT_FOUND_FIND_POST_COMMENT));
    }

    private void updateCommentImages(FindPostComment comment, List<ImageService.ImageMeta> addedImageMetas, Set<String> deletedImageUrls) {
        List<FindPostCommentImage> commentImages = createCommentImages(addedImageMetas, comment);
        commentImageRepository.saveAll(commentImages);
        commentImageRepository.deleteByImageUrlIn(deletedImageUrls);
    }


    private List<FindPostCommentImage> createCommentImages(Collection<ImageService.ImageMeta> imageMetas, FindPostComment comment) {
        return imageMetas.stream()
                .map(imageMeta -> {
                    return FindPostCommentImage.builder()
                            .imageUrl(imageMeta.getImageUrl())
                            .originalFilename(imageMeta.getFileMeta().getOriginalFilename())
                            .comment(comment)
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

    private void verifyOwnership(FindPostComment comment, Long userId) {
        if (!comment.isAuthor(userId)) {
            throw new FindMyMemeException(ErrorCode.FORBIDDEN);
        }
    }

    private void updateFindPostComment(FindPostComment comment, String content, String htmlContent) {
        comment.changeContent(content);
        comment.changeHtmlContent(htmlContent);
    }
}
