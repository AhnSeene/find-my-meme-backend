package com.findmymeme.memepost.service;

import com.findmymeme.common.dto.UserProfileResponse;
import com.findmymeme.exception.ErrorCode;
import com.findmymeme.exception.FindMyMemeException;
import com.findmymeme.file.domain.FileMeta;
import com.findmymeme.file.repository.FileMetaRepository;
import com.findmymeme.file.service.FileStorageService;
import com.findmymeme.memepost.domain.MemePost;
import com.findmymeme.memepost.domain.MemePostSort;
import com.findmymeme.memepost.dto.*;
import com.findmymeme.memepost.dto.Sort;
import com.findmymeme.memepost.repository.MemePostLikeRepository;
import com.findmymeme.memepost.repository.MemePostRepository;
import com.findmymeme.response.MySlice;
import com.findmymeme.tag.repository.MemePostTagRepository;
import com.findmymeme.tag.service.MemePostTagService;
import com.findmymeme.user.domain.User;
import com.findmymeme.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.*;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemePostService {

    private static final int LIMIT_RECOMMENDED_POST = 20;

    private final UserRepository userRepository;
    private final MemePostRepository memePostRepository;
    private final MemePostTagService memePostTagService;
    private final MemePostTagRepository memePostTagRepository;
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
        List<String> tagNames = memePostTagService.applyTagsToPost(request.getTags(), memePost);
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

    public Slice<MemePostSummaryResponse> getMemePosts(int page, int size, MemePostSort postSort, Long userId) {
        Pageable pageable = PageRequest.of(page, size, postSort.toSort());
        Slice<MemePostSummaryResponse> memePosts = memePostRepository.findMemePostSummariesWithLike(pageable, userId);

        memePosts.forEach(response -> response.setTags(getTagNames(response.getId())));
        return memePosts;
    }

    public Slice<MemePostSummaryResponse> getMemePosts(int page, int size, MemePostSort postSort) {
        Pageable pageable = PageRequest.of(page, size, postSort.toSort());
        Slice<MemePost> memePosts = memePostRepository.findSliceAll(pageable);
        List<MemePostSummaryResponse> responses = memePosts.stream()
                .map(memePost -> new MemePostSummaryResponse(memePost, false, getTagNames(memePost.getId())))
                .toList();
        return new SliceImpl<>(responses, pageable, memePosts.hasNext());
    }

    public Slice<MemePostSummaryResponse> searchMemePosts(int page, int size, MemePostSearchCond searchCond) {
        Pageable pageable = PageRequest.of(page, size);
        Slice<MemePostSummaryResponse> responses = memePostRepository.searchByCond(pageable, searchCond);
        responses.forEach(response -> response.setTags(getTagNames(response.getId())));
        return responses;
    }

    public Slice<MemePostSummaryResponse> searchMemePosts(int page, int size, Long userId, MemePostSearchCond searchCond) {
        Pageable pageable = PageRequest.of(page, size);
        Slice<MemePostSummaryResponse> responses = memePostRepository.searchByCondWithMemePostLike(pageable, searchCond, userId);
        responses.forEach(response -> response.setTags(getTagNames(response.getId())));
        return responses;
    }

    public List<MemePostSummaryResponse> getRecommendedPosts(Long memePostId) {
        MemePost memePost = getMemePostById(memePostId);

        List<String> tagNames = getTagNames(memePostId);
        List<MemePost> recommendedPosts = memePostRepository.findByTagNames(tagNames, PageRequest.of(0, LIMIT_RECOMMENDED_POST));
        return recommendedPosts.stream()
                .map(post -> new MemePostSummaryResponse(memePost, false, getTagNames(memePost.getId())))
                .toList();
    }

    public List<MemePostSummaryResponse> getRecommendedPosts(Long memePostId, Long userId) {
        MemePost memePost = getMemePostById(memePostId);

        List<MemePostSummaryResponse> recommendedPosts = memePostRepository.findByTagNamesWithLikeByUserId(getTagNames(memePost.getId()), PageRequest.of(0, LIMIT_RECOMMENDED_POST), userId);
        recommendedPosts.forEach(response -> response.setTags(getTagNames(response.getId())));
        return recommendedPosts;
    }

    @Transactional
    public void softDelete(Long memePostId, Long userId) {
        User user = getUserById(userId);
        MemePost memePost = getMemePostWithUserById(memePostId);
        verifyOwnership(memePost, user);
        memePost.softDelete();
    }

    @Transactional
    public MemePostDownloadDto download(Long memePostId) {
        MemePost memePost = getMemePostById(memePostId);
        memePost.incrementDownloadCount();
        return MemePostDownloadDto.builder()
                .filename(fileStorageService.getFilename(memePost.getImageUrl()))
                .resource(fileStorageService.downloadFile(memePost.getImageUrl()))
                .build();
    }

    public MemePostUserSummaryResponse getMemePostsByAuthorId(int page, int size, String authorName) {
        User author = userRepository.findByUsername(authorName)
                .orElseThrow(() -> new FindMyMemeException(ErrorCode.NOT_FOUND_USER));

        UserProfileResponse userProfileResponse = new UserProfileResponse(author);
        Pageable pageable = PageRequest.of(page, size);
        Slice<MemePost> memePostSummaries = memePostRepository.findMemePostByUserId(pageable, authorName);
        List<MemePostSummaryResponse> responses = memePostSummaries.stream()
                .map(memePost -> new MemePostSummaryResponse(memePost, false, getTagNames(memePost.getId())))
                .toList();
        return MemePostUserSummaryResponse.builder()
                .user(userProfileResponse)
                .memePosts(new MySlice<>(new SliceImpl<>(responses, pageable, memePostSummaries.hasNext())))
                .build();
    }

    public MemePostUserSummaryResponse getMemePostsByAuthorId(int page, int size, String authorName, Long userId) {
        User author = userRepository.findByUsername(authorName)
                .orElseThrow(() -> new FindMyMemeException(ErrorCode.NOT_FOUND_USER));

        UserProfileResponse userProfileResponse = new UserProfileResponse(author);
        Pageable pageable = PageRequest.of(page, size);
        Slice<MemePostSummaryResponse> memePostSummaries = memePostRepository.findMemePostSummariesWithLikeByUserId(pageable, authorName, userId);
        memePostSummaries.forEach(response -> response.setTags(getTagNames(response.getId())));
        return MemePostUserSummaryResponse.builder()
                .user(userProfileResponse)
                .memePosts(new MySlice<>(memePostSummaries))
                .build();
    }

    public List<MemePostSummaryResponse> getRankedPostsAllPeriod(int page, int size, Sort sort) {
        Pageable pageable = PageRequest.of(page, size);
        List<MemePost> memePosts = null;
        if (sort.equals(Sort.LIKE)) {
            memePosts = memePostRepository.findTopByLikeCount(pageable);
        }
        if (sort.equals(Sort.VIEW)) {
            memePosts = memePostRepository.findTopByViewCount(pageable);
        }
        return memePosts.stream()
                .map(post -> new MemePostSummaryResponse(post, false, getTagNames(post.getId())))
                .toList();
    }

    public List<MemePostSummaryResponse> getRankedPostsWithPeriod(int page, int size, Period period) {
        LocalDateTime startDateTime = period.getStartDateTime();
        LocalDateTime endDateTime = period.getEndDateTime();
        Pageable pageable = PageRequest.of(page, size);
        List<MemePost> memePosts = memePostRepository.findTopByLikeCountWithinPeriod(startDateTime, endDateTime, pageable);
        
        return memePosts.stream()
                .map(post -> new MemePostSummaryResponse(post, false, getTagNames(post.getId())))
                .toList();
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

    private MemePost getMemePostById(Long memePostId) {
        return memePostRepository.findById(memePostId)
                .orElseThrow(() -> new FindMyMemeException(ErrorCode.NOT_FOUND_MEME_POST));
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
        return memePostTagService.getTagNames(memePostId);
    }

    private List<Long> getTagIds(Long memePostId) {
        return memePostTagService.getTagIds(memePostId);
    }

    private void verifyOwnership(MemePost memePost, User user) {
        if (!memePost.isOwner(user)) {
            throw new FindMyMemeException(ErrorCode.FORBIDDEN);
        }
    }

}
