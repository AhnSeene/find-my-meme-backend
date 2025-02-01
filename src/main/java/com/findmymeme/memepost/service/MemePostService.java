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
import com.findmymeme.memepost.dto.MemePostTagProjection;
import com.findmymeme.response.MySlice;
import com.findmymeme.memepost.repository.MemePostTagRepository;
import com.findmymeme.user.domain.User;
import com.findmymeme.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemePostService {


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


    public MemePostGetResponse getMemePost(Long memePostId, Optional<Long> userId) {
        return userId.map(id -> getMemePostForUser(memePostId, id))
                .orElseGet(() -> getMemePostForGuest(memePostId));
    }

    @Transactional
    public MemePostGetResponse getMemePostForGuest(Long memePostId) {
        MemePost memePost = getMemePostWithUserById(memePostId);
        List<String> tagNames = getTagNames(memePostId);

        memePost.incrementViewCount();
        return new MemePostGetResponse(memePost, false, false, tagNames);
    }

    @Transactional
    public MemePostGetResponse getMemePostForUser(Long memePostId, Long userId) {
        User user = getUserById(userId);
        MemePost memePost = getMemePostWithUserById(memePostId);
        boolean isLiked = memePostLikeRepository.existsByMemePostIdAndUserId(memePostId, userId);
        List<String> tagNames = getTagNames(memePostId);

        memePost.incrementViewCount();
        return new MemePostGetResponse(memePost, memePost.isOwner(user), isLiked, tagNames);
    }

    public MemePostGetResponse getMemePost2(Long memePostId, Optional<Long> userId) {
        return userId.map(id -> getMemePostForUser2(memePostId, id))
                .orElseGet(() -> getMemePostForGuest(memePostId));
    }

    @Transactional
    public MemePostGetResponse getMemePostForUser2(Long memePostId, Long userId) {
        User user = getUserById(userId);
        MemePost memePost = memePostRepository.findByIdWithTags(memePostId)
                .orElseThrow(() -> new FindMyMemeException(ErrorCode.NOT_FOUND_MEME_POST));
        boolean isLiked = memePostLikeRepository.existsByMemePostIdAndUserId(memePostId, userId);

        memePost.incrementViewCount();
        return new MemePostGetResponse(memePost, memePost.isOwner(user), isLiked, memePost.getTagNames());
    }

    public Slice<MemePostSummaryResponse> searchMemePosts(int page, int size, MemePostSort sort, MemePostSearchCond searchCond, Optional<Long> userId) {
        return userId.map(id -> searchMemePostsForUser(page, size, sort, searchCond, id))
                .orElseGet(() -> searchMemePostsForGuest(page, size, sort, searchCond));
    }

    public Slice<MemePostSummaryResponse> searchMemePostsForGuest(int page, int size, MemePostSort sort, MemePostSearchCond searchCond) {
        Pageable pageable = PageRequest.of(page, size, sort.toSort());

        Slice<Long> postIdSlice = memePostRepository.searchByCond(pageable, searchCond);
        List<Long> postIds = postIdSlice.getContent();

        if (postIds.isEmpty()) {
            return new SliceImpl<>(Collections.emptyList(), pageable, postIdSlice.hasNext());
        }

        List<MemePostSummaryProjection> postDetails = memePostRepository.findPostDetailsByPostIds(postIds);
        List<MemePostTagProjection> memePostsWithTags = memePostTagRepository.findTagNamesByPostId(postIds);

        Map<Long, List<String>> tagsGroupedByPostId = memePostsWithTags.stream()
                .collect(Collectors.groupingBy(
                        MemePostTagProjection::getMemePostId,
                        Collectors.mapping(MemePostTagProjection::getTagName, Collectors.toList())
                ));


        List<MemePostSummaryResponse> updatedResponses = postDetails.stream()
                .map(post -> MemePostSummaryResponse.builder()
                        .id(post.getId())
                        .imageUrl(post.getImageUrl())
                        .likeCount(post.getLikeCount())
                        .viewCount(post.getViewCount())
                        .downloadCount(post.getDownloadCount())
                        .isLiked(false)
                        .tags(tagsGroupedByPostId.getOrDefault(post.getId(), Collections.emptyList()))
                        .build())
                .toList();

        return new SliceImpl<>(updatedResponses, pageable, postIdSlice.hasNext());
    }

    public Slice<MemePostSummaryResponse> searchMemePostsForUser(int page, int size, MemePostSort sort, MemePostSearchCond searchCond, Long userId) {
        Pageable pageable = PageRequest.of(page, size, sort.toSort());

        Slice<Long> postIdSlice = memePostRepository.searchByCond(pageable, searchCond);
        List<Long> postIds = postIdSlice.getContent();

        if (postIds.isEmpty()) {
            return new SliceImpl<>(Collections.emptyList(), pageable, postIdSlice.hasNext());
        }

        List<MemePostSummaryProjection> postDetails = memePostRepository.findPostDetailsByPostIds(postIds);
        List<MemePostTagProjection> memePostsWithTags = memePostTagRepository.findTagNamesByPostId(postIds);

        Map<Long, List<String>> tagsGroupedByPostId = memePostsWithTags.stream()
                .collect(Collectors.groupingBy(
                        MemePostTagProjection::getMemePostId,
                        Collectors.mapping(MemePostTagProjection::getTagName, Collectors.toList())
                ));

        Set<Long> likedPostIds = new HashSet<>(memePostLikeRepository.findLikedPostIds(postIds, userId));

        List<MemePostSummaryResponse> updatedResponses = postDetails.stream()
                .map(post -> MemePostSummaryResponse.builder()
                        .id(post.getId())
                        .imageUrl(post.getImageUrl())
                        .likeCount(post.getLikeCount())
                        .viewCount(post.getViewCount())
                        .downloadCount(post.getDownloadCount())
                        .isLiked(likedPostIds.contains(post.getId()))
                        .tags(tagsGroupedByPostId.getOrDefault(post.getId(), Collections.emptyList()))
                        .build())
                .toList();

        return new SliceImpl<>(updatedResponses, pageable, postIdSlice.hasNext());
    }

    public List<MemePostSummaryResponse> getRecommendedPosts(Long memePostId, int size, Optional<Long> userId) {
        return userId.map(id -> getRecommendedPostsForUser(memePostId, size, id))
                .orElseGet(() -> getRecommendedPostsForGuest(memePostId, size));
    }

    public List<MemePostSummaryResponse> getRecommendedPostsForGuest(Long memePostId, int size) {
        MemePost memePost = getMemePostById(memePostId);
        List<String> tagNames = getTagNames(memePostId); //N + 1문제 태그 불러오기
        List<MemePost> recommendedPosts = memePostRepository.findRelatedPostsByTagNames(tagNames, memePostId, PageRequest.of(0, size));
        return recommendedPosts.stream()
                .map(post -> new MemePostSummaryResponse(memePost, false, getTagNames(memePost.getId())))
                .toList(); //여기서도 n + 1문제
    }


    public List<MemePostSummaryResponse> getRecommendedPostsForUser(Long memePostId, int size, Long userId) {
        MemePost memePost = getMemePostById(memePostId);
        User user = getUserById(userId);
        List<MemePostSummaryResponse> recommendedPosts = memePostRepository.findByTagNamesWithLikeByUserId(getTagNames(memePost.getId()), PageRequest.of(0, size), userId);
        recommendedPosts.forEach(response -> response.setTags(getTagNames(response.getId())));
        return recommendedPosts;
    }

    public List<MemePostSummaryResponse> getRecommendedPosts2(Long memePostId, int size, Optional<Long> userId) {
        return userId.map(id -> getRecommendedPostsForUser2(memePostId, size, id))
                .orElseGet(() -> getRecommendedPostsForGuest2(memePostId, size));
    }


    public List<MemePostSummaryResponse> getRecommendedPostsForGuest2(Long memePostId, int size) {
        MemePost memePost = getMemePostById(memePostId);
        //fetch join으로 memePost의 태그 정보 미리 가져오기
        memePostTagRepository.findAllByMemePostId(memePost.getId());
        //tag 이름으로 관련있는 포스트 찾기
        List<MemePost> recommendedPosts = memePostRepository.findRelatedPostsByTagNames(memePost.getTagNames(), memePostId, PageRequest.of(0, size));
        memePostTagRepository.findTagsByMemePostIdIn(getPostIds(recommendedPosts));
        return recommendedPosts.stream()
                .map(mp -> new MemePostSummaryResponse(mp, false, mp.getTagNames()))
                .toList();
    }

    public List<MemePostSummaryResponse> getRecommendedPosts3(Long memePostId, int size, Optional<Long> userId) {
        return userId.map(id -> getRecommendedPostsForUser3(memePostId, size, id))
                .orElseGet(() -> getRecommendedPostsForGuest3(memePostId, size));
    }

    public List<MemePostSummaryResponse> getRecommendedPostsForGuest3(Long memePostId, int size) {
        MemePost memePost = memePostRepository.findByIdWithTags(memePostId)
                .orElseThrow(() -> new FindMyMemeException(ErrorCode.NOT_FOUND_MEME_POST));
        //fetch join으로 memePost의 태그 정보 미리 가져오기
        //tag 이름으로 관련있는 포스트 찾기
        List<MemePost> recommendedPosts = memePostRepository.findRelatedPostsByTagNames(memePost.getTagNames(), memePostId, PageRequest.of(0, size));
        memePostRepository.findAllWithTagsInPostIds(getPostIds(recommendedPosts));
        return recommendedPosts.stream()
                .map(mp -> new MemePostSummaryResponse(mp, false, mp.getTagNames()))
                .toList();
    }

    public List<MemePostSummaryResponse> getRecommendedPosts4(Long memePostId, int size, Optional<Long> userId) {
        return userId.map(id -> getRecommendedPostsForUser(memePostId, size, id))
                .orElseGet(() -> getRecommendedPostsForGuest4(memePostId, size));
    }

    public List<MemePostSummaryResponse> getRecommendedPostsForGuest4(Long memePostId, int size) {
        MemePost memePost = memePostRepository.findByIdWithTags(memePostId)
                .orElseThrow(() -> new FindMyMemeException(ErrorCode.NOT_FOUND_MEME_POST));
        //fetch join으로 memePost의 태그 정보 미리 가져오기
        //tag 이름으로 관련있는 포스트 찾기
        List<Long> recommendedPostIds = memePostRepository.findRelatedPostIdsByTagNames(memePost.getTagNames(), memePostId, PageRequest.of(0, size));
        List<MemePost> recommendedPostsWithTags = memePostRepository.findAllWithTagsInPostIds(recommendedPostIds);
        return recommendedPostsWithTags.stream()
                .map(mp -> new MemePostSummaryResponse(mp, false, mp.getTagNames()))
                .toList();
    }

    public List<MemePostSummaryResponse> getRecommendedPostsForUser2(Long memePostId, int size, Long userId) {
        MemePost memePost = getMemePostById(memePostId);
        User user = getUserById(userId);
        memePostTagRepository.findAllByMemePostId(memePostId);
        List<MemePost> recommendedPosts = memePostRepository.findRelatedPostsByTagNames(memePost.getTagNames(), memePostId, PageRequest.of(0, size));
        List<Long> likedPostIds = memePostLikeRepository.findLikedPostIds(getPostIds(recommendedPosts), userId);
        return recommendedPosts.stream()
                .map(mp -> new MemePostSummaryResponse(mp, likedPostIds.contains(mp.getId()), mp.getTagNames()))
                .toList();
    }

    public List<MemePostSummaryResponse> getRecommendedPostsForUser3(Long memePostId, int size, Long userId) {
        MemePost memePost = memePostRepository.findByIdWithTags(memePostId)
                .orElseThrow(() -> new FindMyMemeException(ErrorCode.NOT_FOUND_MEME_POST));
        //fetch join으로 memePost의 태그 정보 미리 가져오기
        User user = getUserById(userId);
        //tag 이름으로 관련있는 포스트 찾기
        List<Long> recommendedPostIds = memePostRepository.findRelatedPostIdsByTagNames(memePost.getTagNames(), memePostId, PageRequest.of(0, size));
        List<MemePost> recommendedPostsWithTags = memePostRepository.findAllWithTagsInPostIds(recommendedPostIds);
        List<Long> likedPostIds = memePostLikeRepository.findLikedPostIds(recommendedPostIds, userId);
        return recommendedPostsWithTags.stream()
                .map(mp -> new MemePostSummaryResponse(mp, likedPostIds.contains(mp.getId()), mp.getTagNames()))
                .toList();
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

    public MemePostUserSummaryResponse getMemePostsByAuthorName(int page, int size, String authorName, Optional<Long> userId) {
        return userId.map(id -> getMemePostsByAuthorNameForUser(page, size, authorName, id))
                .orElseGet(() -> getMemePostsByAuthorNameForGuest(page, size, authorName));
    }

    public MemePostUserSummaryResponse getMemePostsByAuthorNameForGuest(int page, int size, String authorName) {
        User author = userRepository.findByUsername(authorName)
                .orElseThrow(() -> new FindMyMemeException(ErrorCode.NOT_FOUND_USER));

        UserProfileResponse userProfileResponse = new UserProfileResponse(author);
        Pageable pageable = PageRequest.of(page, size);
        Slice<MemePost> memePostSummaries = memePostRepository.findMemePostByUsername(pageable, authorName);
        List<MemePostSummaryResponse> responses = memePostSummaries.stream()
                .map(mp -> new MemePostSummaryResponse(mp, false, mp.getTagNames()))
                .toList();
        return MemePostUserSummaryResponse.builder()
                .user(userProfileResponse)
                .memePosts(new MySlice<>(new SliceImpl<>(responses, pageable, memePostSummaries.hasNext())))
                .build();
    }

    public MemePostUserSummaryResponse getMemePostsByAuthorNameForUser(int page, int size, String authorName, Long userId) {
        User author = userRepository.findByUsername(authorName)
                .orElseThrow(() -> new FindMyMemeException(ErrorCode.NOT_FOUND_USER));

        UserProfileResponse userProfileResponse = new UserProfileResponse(author);
        Pageable pageable = PageRequest.of(page, size);
        Slice<MemePostSummaryResponse> memePostSummaries = memePostRepository.findMemePostSummariesWithLikeByAuthorNameAndUserId(pageable, authorName, userId);
        memePostSummaries.forEach(response -> response.setTags(getTagNames(response.getId())));
        return MemePostUserSummaryResponse.builder()
                .user(userProfileResponse)
                .memePosts(new MySlice<>(memePostSummaries))
                .build();
    }

    public MemePostUserSummaryResponse getMemePostsByAuthorName2(int page, int size, String authorName, Optional<Long> userId) {
        return userId.map(id -> getMemePostsByAuthorNameForUser2(page, size, authorName, id))
                .orElseGet(() -> getMemePostsByAuthorNameForGuest2(page, size, authorName));
    }

    public MemePostUserSummaryResponse getMemePostsByAuthorNameForGuest2(int page, int size, String authorName) {
        User author = userRepository.findByUsername(authorName)
                .orElseThrow(() -> new FindMyMemeException(ErrorCode.NOT_FOUND_USER));

        UserProfileResponse userProfileResponse = new UserProfileResponse(author);
        Pageable pageable = PageRequest.of(page, size);
        Slice<Long> postIdSlice = memePostRepository.findSliceByUsername(pageable, authorName);
        List<Long> postIds = postIdSlice.getContent();
        List<MemePost> memePostsWithTags = memePostRepository.findAllWithTagsInPostIds(postIds);
        List<MemePostSummaryResponse> responses = memePostsWithTags.stream()
                .sorted(Comparator.comparing(mp -> postIds.indexOf(mp.getId())))
                .map(mp -> new MemePostSummaryResponse(mp, false, mp.getTagNames()))
                .toList();
        return MemePostUserSummaryResponse.builder()
                .user(userProfileResponse)
                .memePosts(new MySlice<>(new SliceImpl<>(responses, pageable, postIdSlice.hasNext())))
                .build();
    }


    public MemePostUserSummaryResponse getMemePostsByAuthorNameForUser2(int page, int size, String authorName, Long userId) {
        User author = userRepository.findByUsername(authorName)
                .orElseThrow(() -> new FindMyMemeException(ErrorCode.NOT_FOUND_USER));
        User user = getUserById(userId);
        UserProfileResponse userProfileResponse = new UserProfileResponse(author);

        Pageable pageable = PageRequest.of(page, size);
        Slice<Long> postIdSlice = memePostRepository.findSliceByUsername(pageable, authorName);
        List<Long> postIds = postIdSlice.getContent();

        List<MemePost> memePostsWithTags = memePostRepository.findAllWithTagsInPostIds(postIds);
        Set<Long> likedPostIds = new HashSet<>(memePostLikeRepository.findLikedPostIds(postIds, userId));

        List<MemePostSummaryResponse> memePostSummaryResponses = memePostsWithTags.stream()
                .sorted(Comparator.comparing(mp -> postIds.indexOf(mp.getId())))
                .map(mp -> new MemePostSummaryResponse(mp, likedPostIds.contains(mp.getId()), mp.getTagNames()))
                .toList();
        return MemePostUserSummaryResponse.builder()
                .user(userProfileResponse)
                .memePosts(new MySlice<>(new SliceImpl<>(memePostSummaryResponses, pageable, postIdSlice.hasNext())))
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

    private void verifyOwnership(MemePost memePost, User user) {
        if (!memePost.isOwner(user)) {
            throw new FindMyMemeException(ErrorCode.FORBIDDEN);
        }
    }

}
