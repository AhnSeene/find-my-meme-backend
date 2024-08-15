package com.findmymeme.findpost.service;

import com.findmymeme.exception.ErrorCode;
import com.findmymeme.exception.FindMyMemeException;
import com.findmymeme.file.domain.FileMeta;
import com.findmymeme.file.service.ImageService;
import com.findmymeme.findpost.domain.FindPost;
import com.findmymeme.findpost.domain.FindPostComment;
import com.findmymeme.findpost.domain.FindPostCommentImage;
import com.findmymeme.findpost.dto.*;
import com.findmymeme.findpost.repository.FindPostCommentImageRepository;
import com.findmymeme.findpost.repository.FindPostCommentRepository;
import com.findmymeme.findpost.repository.FindPostRepository;
import com.findmymeme.user.User;
import com.findmymeme.user.UserRepository;
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
        FindPostComment comment = createFindPostComment(request, findPost, user);
        Document doc = Jsoup.parse(request.getHtmlContent());
        List<ImageService.ImageMeta> imageMetas = imageService.convertAndMoveImageUrls(doc);
        List<FindPostCommentImage> commentImages = createCommentImages(imageMetas, comment);

        comment.changeHtmlContent(doc.body().html());
        FindPostComment savedComment = commentRepository.save(comment);
        commentImageRepository.saveAll(commentImages);

        return new FindPostCommentAddResponse(savedComment);
    }

    private FindPostComment createFindPostComment(FindPostCommentAddRequest request, FindPost findPost, User user) {
        FindPostComment parentComment = null;
        if (request.getParentCommentId() != null) {
            parentComment = getParentCommentById(request.getParentCommentId());
        }
        return FindPostComment.builder()
                .content(request.getContent())
                .parentComment(parentComment)
                .findPost(findPost)
                .user(user)
                .build();
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

    private boolean isNotOwner(FindPostComment comment, User user) {
        return !comment.isOwner(user);
    }

}
