package com.findmymeme.memepost.service;

import com.findmymeme.exception.ErrorCode;
import com.findmymeme.exception.FindMyMemeException;
import com.findmymeme.file.domain.FileMeta;
import com.findmymeme.file.repository.FileMetaRepository;
import com.findmymeme.file.service.FileStorageService;
import com.findmymeme.memepost.domain.MemePost;
import com.findmymeme.memepost.dto.MemePostGetResponse;
import com.findmymeme.memepost.dto.MemePostSummaryResponse;
import com.findmymeme.memepost.repository.MemePostLikeRepository;
import com.findmymeme.memepost.repository.MemePostRepository;
import com.findmymeme.memepost.dto.MemePostUploadRequest;
import com.findmymeme.memepost.dto.MemePostUploadResponse;
import com.findmymeme.tag.service.PostTagService;
import com.findmymeme.user.domain.User;
import com.findmymeme.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.findmymeme.tag.domain.PostType.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemePostService {

    private final UserRepository userRepository;
    private final MemePostRepository memePostRepository;
    private final PostTagService postTagService;
    private final FileStorageService fileStorageService;
    private final FileMetaRepository fileMetaRepository;
    private final MemePostLikeRepository memePostLikeRepository;

    @Transactional
    public MemePostUploadResponse uploadMemePost(MemePostUploadRequest request, Long userId) {
        User user = getUserById(userId);

        FileMeta fileMeta = findFileMetaByFileUrl(request.getImageUrl());
        String permanentImageUrl = fileStorageService.moveFileToPermanent(request.getImageUrl());

        MemePost memePost = createMemePost(permanentImageUrl, user, fileMeta);
        memePostRepository.save(memePost);
        List<String> tagNames = postTagService.applyTagsToPost(request.getTags(), memePost.getId(), MEME_POST);
        return new MemePostUploadResponse(memePost.getImageUrl(), tagNames);
    }

    @Transactional
    public MemePostGetResponse getMemePost(Long memePostId) {
        MemePost memePost = getMemePostWithUserById(memePostId);
        List<String> tagNames = getTagNames(memePostId);

        memePost.incrementViewCount();
        return new MemePostGetResponse(memePost, false, false, tagNames);
    }

    @Transactional
    public MemePostGetResponse getMemePost(Long memePostId, Long userId) {
        User user = getUserById(userId);
        MemePost memePost = getMemePostWithUserById(memePostId);
        boolean isLiked = memePostLikeRepository.existsByMemePostIdAndUserId(memePostId, userId);
        List<String> tagNames = getTagNames(memePostId);

        memePost.incrementViewCount();
        return new MemePostGetResponse(memePost, memePost.isOwner(user), isLiked, tagNames);
    }

    public Slice<MemePostSummaryResponse> getMemePosts(int page, int size, Long userId) {
        Pageable pageable = PageRequest.of(page, size);
        Slice<MemePostSummaryResponse> memePosts = memePostRepository.findMemePostSummariesWithLike(pageable, userId);

        memePosts.forEach(response -> response.setTags(getTagNames(response.getId())));
        return memePosts;
    }

    public Slice<MemePostSummaryResponse> getMemePosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Slice<MemePost> memePosts = memePostRepository.findSliceAll(pageable);
        List<MemePostSummaryResponse> responses = memePosts.stream()
                .map(memePost -> new MemePostSummaryResponse(memePost, false, getTagNames(memePost.getId())))
                .toList();
        return new SliceImpl<>(responses, pageable, memePosts.hasNext());
    }

    @Transactional
    public void softDelete(Long memePostId, Long userId) {
        User user = getUserById(userId);
        MemePost memePost = getMemePostWithUserById(memePostId);
        verifyOwnership(memePost, user);
        memePost.softDelete();
    }

    public Slice<MemePostSummaryResponse> getMyMemePosts(int page, int size, Long userId) {
        Pageable pageable = PageRequest.of(page, size);
        return memePostRepository.findMemePostSummariesWithLikeByUserId(pageable, userId, userId);
    }

    public Slice<MemePostSummaryResponse> getMemePostsByAuthorId(int page, int size, Long authorId, Long userId) {
        Pageable pageable = PageRequest.of(page, size);
        return memePostRepository.findMemePostSummariesWithLikeByUserId(pageable, authorId, userId);
    }

    private MemePost createMemePost(String permanentImageUrl, User user, FileMeta fileMeta) {
        return MemePost.builder()
                .imageUrl(permanentImageUrl)
                .user(user)
                .size(fileMeta.getSize())
                .originalFilename(fileMeta.getOriginalFilename())
                .extension(fileMeta.getExtension())
                .resolution(fileMeta.getResolution())
                .build();
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new FindMyMemeException(ErrorCode.NOT_FOUND_USER));
    }

    private FileMeta findFileMetaByFileUrl(String fileUrl) {
        return fileMetaRepository.findByFileUrl(fileUrl)
                .orElseThrow(() -> new FindMyMemeException(ErrorCode.NOT_FOUND_FILE_META));
    }

    private MemePost getMemePostWithUserById(Long memePostId) {
        return memePostRepository.findWithUserById(memePostId)
                .orElseThrow(() -> new FindMyMemeException(ErrorCode.NOT_FOUND_FIND_POST));
    }

    private List<String> getTagNames(Long memePostId) {
        return postTagService.getTagNames(memePostId, MEME_POST);
    }

    private void verifyOwnership(MemePost memePost, User user) {
        if (!memePost.isOwner(user)) {
            throw new FindMyMemeException(ErrorCode.FORBIDDEN);
        }
    }

}
