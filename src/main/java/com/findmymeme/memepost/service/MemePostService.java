package com.findmymeme.memepost.service;

import com.findmymeme.common.dto.UserProfileResponse;
import com.findmymeme.exception.ErrorCode;
import com.findmymeme.exception.FindMyMemeException;
import com.findmymeme.file.domain.FileMeta;
import com.findmymeme.file.domain.FileType;
import com.findmymeme.file.repository.FileMetaRepository;
import com.findmymeme.file.service.FileStorageService;
import com.findmymeme.memepost.domain.MemePost;
import com.findmymeme.memepost.domain.MemePostSort;
import com.findmymeme.memepost.domain.ProcessingStatus;
import com.findmymeme.memepost.dto.*;
import com.findmymeme.memepost.dto.Sort;
import com.findmymeme.memepost.repository.MemePostLikeRepository;
import com.findmymeme.memepost.repository.MemePostRepository;
import com.findmymeme.memepost.dto.MemePostTagProjection;
import com.findmymeme.notification.domain.MemePostUploadFailEvent;
import com.findmymeme.notification.domain.MemePostUploadSuccessEvent;
import com.findmymeme.response.MySlice;
import com.findmymeme.memepost.repository.MemePostTagRepository;
import com.findmymeme.user.domain.User;
import com.findmymeme.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemePostService {

    @Value("${file.base-url}")
    private String fileBaseUrl;

    private final UserRepository userRepository;
    private final MemePostRepository memePostRepository;
    private final MemePostTagService memePostTagService;
    private final MemePostTagRepository memePostTagRepository;
    private final FileStorageService fileStorageService;
    private final FileMetaRepository fileMetaRepository;
    private final MemePostLikeRepository memePostLikeRepository;
    private final MemePostViewCountService memePostViewCountService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public MemePostUploadResponse uploadMemePost(MemePostUploadRequest request, Long userId) {
        User user = getUserById(userId);

        FileMeta fileMeta = findFileMetaByFileUrl(request.getImageUrl());
        String permanentImageUrl = fileStorageService.moveFileToPermanent(request.getImageUrl(), FileType.MEME);
        try {
            MemePost memePost = createMemePost(permanentImageUrl, user, fileMeta);
            MemePost savedMemePost = memePostRepository.save(memePost);
            List<String> tagNames = memePostTagService.applyTagsToPost(request.getTags(), savedMemePost);

            eventPublisher.publishEvent(new MemePostCreatedEvent(savedMemePost.getId(), userId, permanentImageUrl));
            return new MemePostUploadResponse(memePost.getImageUrl(), tagNames);
        }
        catch (Exception e) {
            log.warn("MemePost 저장 실패: userId={}, permanentImageUrl={}", userId, permanentImageUrl, e);
            throw e;
        }
    }

    @Transactional
    public void updatePostAfterProcessing(Long memePostId, Long userId, String thumbnail288Url, String thumbnail657Url) {
        MemePost memePost = getMemePostById(memePostId);
        memePost.updateThumbnails(thumbnail288Url, thumbnail657Url);
        memePost.changeProcessingStatus(ProcessingStatus.READY);
        log.info("Create MemePostUploadSuccessEvent event memePostId: {} userId : {}", memePostId, userId);
        eventPublisher.publishEvent(new MemePostUploadSuccessEvent(userId, memePostId));
    }


    @Transactional
    public void updatePostToFailed(Long memePostId, Long userId, String errorMessage) {
        memePostRepository.findById(memePostId).ifPresent(memePost -> {
            memePost.changeProcessingStatus(ProcessingStatus.FAILED);
            log.warn("MemePost(id:{}) 상태를 FAILED로 변경합니다. 원인: {}", memePostId, errorMessage);
        });
        eventPublisher.publishEvent(new MemePostUploadFailEvent(userId, memePostId));
    }

    @Transactional
    public MemePostGetResponse getMemePost(Long memePostId, Optional<Long> userId) {
        MemePost memePost = memePostRepository.findByIdWithTags(memePostId)
                .orElseThrow(() -> new FindMyMemeException(ErrorCode.NOT_FOUND_MEME_POST));

        memePostViewCountService.incrementViewCount(memePostId);

        return userId.map(id -> createUserGetResponse(memePost, id))
                .orElseGet(() -> createGuestGetResponse(memePost));
    }

    @Transactional(readOnly = true)
    public MemePostGetResponse getMemePostRedis(Long memePostId, Optional<Long> userId) {
        MemePost memePost = memePostRepository.findByIdWithTags(memePostId)
                .orElseThrow(() -> new FindMyMemeException(ErrorCode.NOT_FOUND_MEME_POST));

        memePostViewCountService.incrementViewCountRedis(memePostId);

        return userId.map(id -> createUserGetResponse(memePost, id))
                .orElseGet(() -> createGuestGetResponse(memePost));
    }

    private MemePostGetResponse createGuestGetResponse(MemePost memePost) {
        return MemePostGetResponse.from(memePost, false, false, fileBaseUrl);
    }

    private MemePostGetResponse createUserGetResponse(MemePost memePost, Long userId) {
        boolean isLiked = memePostLikeRepository.existsByMemePostIdAndUserId(memePost.getId(), userId);
        return MemePostGetResponse.from(memePost, memePost.isOwner(userId), isLiked, fileBaseUrl);
    }

    public Slice<MemePostSummaryResponse> getMemePostsWithLikeInfo(
            int page, int size,
            MemePostSort sort,
            MemePostSearchCond searchCond,
            Optional<Long> userId
    ) {
        Pageable pageable = PageRequest.of(page, size, sort.toSort());

        Slice<Long> postIdSlice = memePostRepository.searchByCond(pageable, searchCond);
        List<Long> postIds = postIdSlice.getContent();

        if (postIds.isEmpty()) {
            return new SliceImpl<>(Collections.emptyList(), pageable, postIdSlice.hasNext());
        }

        List<MemePostSummaryProjection> postDetails = memePostRepository.findPostDetailsByPostIds(postIds);
        Map<Long, List<String>> tagsGroupedByPostId = findTagNamesGroupedByPostIds(postIds);
        Set<Long> likedPostIds = findLikedPostIds(postIds, userId);

        Map<Long, MemePostSummaryProjection> postDetailsMap = postDetails.stream()
                .collect(Collectors.toMap(MemePostSummaryProjection::getId, Function.identity()));

        List<MemePostSummaryProjection> sortedPostDetails = postIds.stream()
                .map(postDetailsMap::get)
                .filter(Objects::nonNull)
                .toList();

        List<MemePostSummaryResponse> memePostSummaries = mapToSummaryResponse(sortedPostDetails, likedPostIds, tagsGroupedByPostId);

        return new SliceImpl<>(memePostSummaries, pageable, postIdSlice.hasNext());
    }

    public List<MemePostSummaryResponse> getRecommendedPostsWithLikeInfo(Long memePostId, int size, Optional<Long> userId) {
        List<Long> tagIds = memePostTagRepository.findTagIdsByPostId(memePostId);

        if (tagIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> recommendedPostIds = memePostRepository.findRelatedPostIdsByTagIds(tagIds, memePostId, PageRequest.of(0, size));

        if (recommendedPostIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<MemePostSummaryProjection> postDetails = memePostRepository.findPostDetailsByPostIds(recommendedPostIds);
        Map<Long, List<String>> tagsGroupedByPostId = findTagNamesGroupedByPostIds(recommendedPostIds);
        Set<Long> likedPostIds = findLikedPostIds(recommendedPostIds, userId);

        Map<Long, MemePostSummaryProjection> postDetailsMap = postDetails.stream()
                .collect(Collectors.toMap(MemePostSummaryProjection::getId, Function.identity()));

        List<MemePostSummaryProjection> sortedPostDetails = recommendedPostIds.stream()
                .map(postDetailsMap::get)
                .filter(Objects::nonNull)
                .toList();

        return mapToSummaryResponse(sortedPostDetails, likedPostIds, tagsGroupedByPostId);
    }

    @Transactional
    public void softDelete(Long memePostId, Long userId) {
        MemePost memePost = getMemePostWithUserById(memePostId);
        verifyOwnership(memePost, userId);
        memePost.softDelete();
    }

    @Transactional
    public MemePostDownloadDto download(Long memePostId) {
        MemePost memePost = getMemePostById(memePostId);
        memePost.incrementDownloadCount();
        return MemePostDownloadDto.builder()
                .filename(fileStorageService.getFilename(memePost.getImageUrl()))
                .presignedUrl(fileStorageService.generatePresignedDownloadUrl(memePost.getImageUrl()))
                .build();
    }

    public MemePostUserSummaryResponse getMemePostsByAuthorNameWithLikeInfo(int page, int size, String authorName, Optional<Long> userId) {
        User author = userRepository.findByUsername(authorName)
                .orElseThrow(() -> new FindMyMemeException(ErrorCode.NOT_FOUND_USER));

        UserProfileResponse userProfileResponse = UserProfileResponse.from(author, fileBaseUrl);
        Pageable pageable = PageRequest.of(page, size);
        Slice<Long> postIdSlice = memePostRepository.findMemePostIdsByUsername(pageable, authorName);
        List<Long> postIds = postIdSlice.getContent();

        if (postIds.isEmpty()) {
            return MemePostUserSummaryResponse.builder()
                    .user(userProfileResponse)
                    .memePosts(new MySlice<>(new SliceImpl<>(Collections.emptyList(), pageable, false)))
                    .build();
        }

        List<MemePostSummaryProjection> postDetails = memePostRepository.findPostDetailsByPostIds(postIds);
        Map<Long, List<String>> tagsGroupedByPostId = findTagNamesGroupedByPostIds(postIds);
        Set<Long> likedPostIds = findLikedPostIds(postIds, userId);

        Map<Long, MemePostSummaryProjection> postDetailsMap = postDetails.stream()
                .collect(Collectors.toMap(MemePostSummaryProjection::getId, Function.identity()));

        List<MemePostSummaryProjection> sortedPostDetails = postIds.stream()
                .map(postDetailsMap::get)
                .filter(Objects::nonNull)
                .toList();

        List<MemePostSummaryResponse> memePostSummaries = mapToSummaryResponse(sortedPostDetails, likedPostIds, tagsGroupedByPostId);
        return MemePostUserSummaryResponse.builder()
                .user(userProfileResponse)
                .memePosts(new MySlice<>(new SliceImpl<>(memePostSummaries, pageable, postIdSlice.hasNext())))
                .build();
    }

    public MemePostUserSummaryResponse getMyMemePosts(int page, int size, Long userId) {
        User user = getUserById(userId);
        UserProfileResponse userProfileResponse = UserProfileResponse.from(user, fileBaseUrl);
        Pageable pageable = PageRequest.of(page, size);

        Slice<Long> postIdSlice = memePostRepository.findMyMemePostIdsByUserId(pageable, userId);
        List<Long> postIds = postIdSlice.getContent();

        if (postIds.isEmpty()) {
            return MemePostUserSummaryResponse.builder()
                    .user(userProfileResponse)
                    .memePosts(new MySlice<>(new SliceImpl<>(Collections.emptyList(), pageable, false)))
                    .build();
        }

        List<MemePostSummaryProjection> postDetails = memePostRepository.findPostDetailsByPostIds(postIds);
        Map<Long, List<String>> tagsGroupedByPostId = findTagNamesGroupedByPostIds(postIds);
        Set<Long> likedPostIds = findLikedPostIds(postIds, Optional.of(userId));

        Map<Long, MemePostSummaryProjection> postDetailsMap = postDetails.stream()
                .collect(Collectors.toMap(MemePostSummaryProjection::getId, Function.identity()));

        List<MemePostSummaryProjection> sortedPostDetails = postIds.stream()
                .map(postDetailsMap::get)
                .filter(Objects::nonNull)
                .toList();

        List<MemePostSummaryResponse> memePostSummaries = mapToSummaryResponse(sortedPostDetails, likedPostIds, tagsGroupedByPostId);
        return MemePostUserSummaryResponse.builder()
                .user(userProfileResponse)
                .memePosts(new MySlice<>(new SliceImpl<>(memePostSummaries, pageable, postIdSlice.hasNext())))
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
                .map(post -> MemePostSummaryResponse.from(post, false, getTagNames(post.getId()), fileBaseUrl))
                .toList();
    }

    public List<MemePostSummaryResponse> getRankedPostsWithPeriod(int page, int size, Period period) {
        LocalDateTime startDateTime = period.getStartDateTime();
        LocalDateTime endDateTime = period.getEndDateTime();
        Pageable pageable = PageRequest.of(page, size);
        List<MemePost> memePosts = memePostRepository.findTopByLikeCountWithinPeriod(startDateTime, endDateTime, pageable);

        return memePosts.stream()
                .map(post -> MemePostSummaryResponse.from(post, false, getTagNames(post.getId()), fileBaseUrl))
                .toList();
    }

    private List<MemePostSummaryResponse> mapToSummaryResponse(
            List<MemePostSummaryProjection> postDetails,
            Set<Long> likedPostIds,
            Map<Long, List<String>> tagsGroupedByPostId
    ) {
        return postDetails.stream()
                .map(post -> {
                    boolean isLiked = likedPostIds.contains(post.getId());
                    List<String> tags = tagsGroupedByPostId.getOrDefault(post.getId(), Collections.emptyList());
                    return MemePostSummaryResponse.from(post, isLiked, tags, fileBaseUrl);
                })
                .toList();
    }

    private Map<Long, List<String>> findTagNamesGroupedByPostIds(List<Long> postIds) {
        List<MemePostTagProjection> memePostsWithTags = memePostTagRepository.findTagNamesInPostIds(postIds);

        return memePostsWithTags.stream()
                .collect(Collectors.groupingBy(
                        MemePostTagProjection::getMemePostId,
                        Collectors.mapping(MemePostTagProjection::getTagName, Collectors.toList())
                ));
    }

    private Set<Long> findLikedPostIds(List<Long> postIds, Optional<Long> userId) {
        return userId
                .map(id -> new HashSet<>(memePostLikeRepository.findLikedPostIds(postIds, id)))
                .orElseGet(HashSet::new);
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

    private List<Long> getPostIds(List<MemePost> memePosts) {
        return memePosts.stream()
                .map(MemePost::getId)
                .toList();
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
                .orElseThrow(() -> new FindMyMemeException(ErrorCode.NOT_FOUND_MEME_POST));
    }

    private List<String> getTagNames(Long memePostId) {
        return memePostTagService.getTagNames(memePostId);
    }

    private void verifyOwnership(MemePost memePost, Long userId) {
        if (!memePost.isOwner(userId)) {
            throw new FindMyMemeException(ErrorCode.AUTH_FORBIDDEN);
        }
    }


}
