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
import com.findmymeme.memepost.repository.MemePostTagDto;
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
        //태그 정보 N + 1, 좋아요 정보 서브 쿼리
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

    public Slice<MemePostSummaryResponse> getMemePosts1(int page, int size, MemePostSort postSort) {
        Pageable pageable = PageRequest.of(page, size, postSort.toSort());
        Slice<MemePost> memePostSlice = memePostRepository.findSliceAll(pageable);
        Map<Long, List<String>> postTagNames = memePostTagRepository.findTagsByMemePostIdIn(getPostIds(memePostSlice.getContent()))
                .stream()
                .collect(groupingBy(tag -> tag.getMemePost().getId(),
                        mapping(tag -> tag.getTag().getName(), toList())));
        List<MemePostSummaryResponse> responses = memePostSlice.getContent().stream()
                .map(mp -> new MemePostSummaryResponse(mp, false, postTagNames.get(mp.getId())))
                .toList();
        return new SliceImpl<>(responses, pageable, memePostSlice.hasNext());
    }

    public Slice<MemePostSummaryResponse> getMemePosts2(int page, int size, MemePostSort postSort, Long userId) {
        User user = getUserById(userId);
        Pageable pageable = PageRequest.of(page, size, postSort.toSort());
        //컬렉션 조회 fetch join시 메모리에서 개수를 해결
        Slice<MemePost> memePostSlice = memePostRepository.findAllWithTags(pageable);
        List<Long> postIds = getPostIds(memePostSlice.getContent());
        //좋아요는 최적화 됨
        Set<Long> likedPostIds = new HashSet<>(memePostRepository.findLikedPostIds(postIds, user));
        List<MemePostSummaryResponse> responses = memePostSlice.getContent().stream()
                .map(mp -> new MemePostSummaryResponse(mp, likedPostIds.contains(mp.getId()), mp.getTagNames()))
                .toList();

        return new SliceImpl<>(responses, pageable, memePostSlice.hasNext());
    }

    public Slice<MemePostSummaryResponse> getMemePosts3(int page, int size, MemePostSort postSort, Long userId) {
        User user = getUserById(userId);
        Pageable pageable = PageRequest.of(page, size, postSort.toSort());
        Slice<MemePost> memePostSlice = memePostRepository.findSliceAll(pageable);
        List<Long> postIds = getPostIds(memePostSlice.getContent());
        Set<Long> likedPostIds = new HashSet<>(memePostRepository.findLikedPostIds(postIds, user));
        //MemePostTag 정보를 batch size를 통해 가져옴 -> 하지만 Tag도 또한 batch size로 하나의 쿼리로 또 나감
        //batch size 설정 안할시 MemePostTag N + 1 이랑 tag까지 또 N+ 1
        List<MemePostSummaryResponse> responses = memePostSlice.getContent().stream()
                .map(mp -> new MemePostSummaryResponse(mp, likedPostIds.contains(mp.getId()), mp.getTagNames()))
                .toList();

        return new SliceImpl<>(responses, pageable, memePostSlice.hasNext());
    }

    public Slice<MemePostSummaryResponse> getMemePosts4(int page, int size, MemePostSort postSort, Long userId) {
        User user = getUserById(userId);
        Pageable pageable = PageRequest.of(page, size, postSort.toSort());
        Slice<MemePost> memePostSlice = memePostRepository.findSliceAll(pageable);
        List<Long> postIds = getPostIds(memePostSlice.getContent());

        // MemePostTag와 Tag를 한번에 dto로 조회 하나의 쿼리로
        Map<Long, List<String>> postTagNames = memePostRepository.findTagNamesByMemePosts(memePostSlice.getContent())
                .stream()
                .collect(groupingBy(MemePostTagDto::getPostId,
                        mapping(MemePostTagDto::getTagName, toList())));
        //좋아요도 하나의 쿼리로
        Set<Long> likedPostIds = new HashSet<>(memePostRepository.findLikedPostIds(postIds, user));
        List<MemePostSummaryResponse> responses = memePostSlice.getContent().stream()
                .map(mp -> new MemePostSummaryResponse(
                        mp,
                        likedPostIds.contains(mp.getId()),
                        postTagNames.getOrDefault(mp.getId(), Collections.emptyList())
                ))
                .toList();

        return new SliceImpl<>(responses, pageable, memePostSlice.hasNext());
    }
    public Slice<MemePostSummaryResponse> getMemePosts5(int page, int size, MemePostSort postSort, Long userId) {
        User user = getUserById(userId);
        Pageable pageable = PageRequest.of(page, size, postSort.toSort());
        Slice<MemePost> memePostSlice = memePostRepository.findSliceAll(pageable);
        List<Long> postIds = getPostIds(memePostSlice.getContent());

        // MemePostTag와 Tag를 한번에 fetch join 하나의 쿼리로 하되, 위와는 차이점이 MemePostTagRepository에서 entitygraph를 통해 fetch join
        Map<Long, List<String>> postTagNames = memePostTagRepository.findTagsByMemePostIdIn(postIds)
                .stream()
                .collect(groupingBy(tag -> tag.getMemePost().getId(),
                        mapping(tag -> tag.getTag().getName(), toList())));
//좋아요도 하나의 쿼리로
        Set<Long> likedPostIds = new HashSet<>(memePostRepository.findLikedPostIds(postIds, user));
        List<MemePostSummaryResponse> responses = memePostSlice.getContent().stream()
                .map(mp -> new MemePostSummaryResponse(
                        mp,
                        likedPostIds.contains(mp.getId()),
                        postTagNames.getOrDefault(mp.getId(), Collections.emptyList())
                ))
                .toList();

        return new SliceImpl<>(responses, pageable, memePostSlice.hasNext());
    }

    public Slice<MemePostSummaryResponse> getMemePosts6(int page, int size, MemePostSort postSort, Long userId) {
        User user = getUserById(userId);
        Pageable pageable = PageRequest.of(page, size, postSort.toSort());
        //id만 먼저 페이징 조회
        Slice<Long> postIdSlice = memePostRepository.findSliceAllIds(pageable);
        List<Long> postIds = postIdSlice.getContent();

        //ID로 MemePost, Tag함께 조회
        List<MemePost> memePostsWithTags = memePostRepository.findAllWithTagsInPostIds(postIds)
                .stream()
                .sorted(Comparator.comparing(mp -> postIds.indexOf(mp.getId())))
                .toList();
        // MemePost를 MemePostTag와 Tag를 한번에 fetch join 하나의 쿼리로 찾기
        Set<Long> likedPostIds = new HashSet<>(memePostRepository.findLikedPostIds(postIds, user));
        List<MemePostSummaryResponse> responses = memePostsWithTags.stream()
                .map(mp -> new MemePostSummaryResponse(
                        mp,
                        likedPostIds.contains(mp.getId()),
                        mp.getTagNames()
                ))
                .toList();

        return new SliceImpl<>(responses, pageable, postIdSlice.hasNext());
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

    public List<MemePostSummaryResponse> getRecommendedPosts(Long memePostId, int size) {
        MemePost memePost = getMemePostById(memePostId);
        List<String> tagNames = getTagNames(memePostId); //N + 1문제 태그 불러오기
        List<MemePost> recommendedPosts = memePostRepository.findRelatedPostsByTagNames(tagNames, memePostId, PageRequest.of(0, size));
        return recommendedPosts.stream()
                .map(post -> new MemePostSummaryResponse(memePost, false, getTagNames(memePost.getId())))
                .toList(); //여기서도 n + 1문제
    }


    public List<MemePostSummaryResponse> getRecommendedPosts(Long memePostId, int size, Long userId) {
        MemePost memePost = getMemePostById(memePostId);
        User user = getUserById(userId);
        List<MemePostSummaryResponse> recommendedPosts = memePostRepository.findByTagNamesWithLikeByUserId(getTagNames(memePost.getId()), PageRequest.of(0, size), userId);
        recommendedPosts.forEach(response -> response.setTags(getTagNames(response.getId())));
        return recommendedPosts;
    }


    public List<MemePostSummaryResponse> getRecommendedPosts2(Long memePostId, int size) {
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

    public List<MemePostSummaryResponse> getRecommendedPosts3(Long memePostId, int size) {
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

    public List<MemePostSummaryResponse> getRecommendedPosts4(Long memePostId, int size) {
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

    public List<MemePostSummaryResponse> getRecommendedPosts2(Long memePostId, int size, Long userId) {
        MemePost memePost = getMemePostById(memePostId);
        User user = getUserById(userId);
        memePostTagRepository.findAllByMemePostId(memePostId);
        List<MemePost> recommendedPosts = memePostRepository.findRelatedPostsByTagNames(memePost.getTagNames(), memePostId, PageRequest.of(0, size));
        List<Long> likedPostIds = memePostRepository.findLikedPostIds(getPostIds(recommendedPosts), user);
        return recommendedPosts.stream()
                .map(mp -> new MemePostSummaryResponse(mp, likedPostIds.contains(mp.getId()), mp.getTagNames()))
                .toList();
    }

    public List<MemePostSummaryResponse> getRecommendedPosts3(Long memePostId, int size, Long userId) {
        MemePost memePost = memePostRepository.findByIdWithTags(memePostId)
                .orElseThrow(() -> new FindMyMemeException(ErrorCode.NOT_FOUND_MEME_POST));
        //fetch join으로 memePost의 태그 정보 미리 가져오기
        User user = getUserById(userId);
        //tag 이름으로 관련있는 포스트 찾기
        List<Long> recommendedPostIds = memePostRepository.findRelatedPostIdsByTagNames(memePost.getTagNames(), memePostId, PageRequest.of(0, size));
        List<MemePost> recommendedPostsWithTags = memePostRepository.findAllWithTagsInPostIds(recommendedPostIds);
        List<Long> likedPostIds = memePostRepository.findLikedPostIds(recommendedPostIds, user);
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
