package com.findmymeme.memepost.service;

import com.findmymeme.file.domain.FileMeta;
import com.findmymeme.file.domain.FileType;
import com.findmymeme.file.repository.FileMetaRepository;
import com.findmymeme.file.service.FileStorageService;
import com.findmymeme.memepost.domain.Extension;
import com.findmymeme.memepost.domain.MemePost;
import com.findmymeme.memepost.domain.Resolution;
import com.findmymeme.memepost.dto.MemePostGetResponse;
import com.findmymeme.memepost.dto.MemePostUploadRequest;
import com.findmymeme.memepost.dto.MemePostUploadResponse;
import com.findmymeme.memepost.repository.MemePostLikeRepository;
import com.findmymeme.memepost.repository.MemePostRepository;
import com.findmymeme.memepost.repository.MemePostTagRepository;
import com.findmymeme.user.domain.User;
import com.findmymeme.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemePostServiceTest {

    @InjectMocks
    private MemePostService memePostService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private MemePostRepository memePostRepository;
    @Mock
    private FileMetaRepository fileMetaRepository;
    @Mock
    private FileStorageService fileStorageService;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private MemePostTagService memePostTagService;



    @BeforeEach
    void setUp() {

    }


    @Test
    @DisplayName("트랜잭션 성공 시: 모든 작업이 성공하면 이벤트가 발행된다")
    void givenSuccess_whenUploadMemePost_thenEventIsPublished() {
        // given
        Long userId = 1L;
        String permanentImageUrl = "images/memes/permanent-image.jpg";
        MemePostUploadRequest request = new MemePostUploadRequest("temp/image.jpg", List.of(1L, 2L));

        User mockUser = User.builder().build();
        FileMeta mockFileMeta = FileMeta.builder()
                .originalFilename("image.jpg")
                .size(1000L)
                .resolution(new Resolution(300, 300))
                .extension("jpg")
                .build();
        MemePost savedMemePost = MemePost.builder().imageUrl(permanentImageUrl).extension(Extension.JPG.getValue()).build();

        ReflectionTestUtils.setField(mockUser, "id", userId);
        ReflectionTestUtils.setField(savedMemePost, "id", 100L);

        given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));
        given(fileMetaRepository.findByFileUrl(anyString())).willReturn(Optional.of(mockFileMeta));
        given(fileStorageService.moveFileToPermanent(any(), any())).willReturn(permanentImageUrl);
        given(memePostRepository.save(any(MemePost.class))).willReturn(savedMemePost);

        // when
        memePostService.uploadMemePost(request, userId);

        // then
        ArgumentCaptor<MemePostCreatedEvent> eventCaptor = ArgumentCaptor.forClass(MemePostCreatedEvent.class);
        verify(eventPublisher, times(1)).publishEvent(eventCaptor.capture());

        MemePostCreatedEvent capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.getMemePostId()).isEqualTo(100L);
        assertThat(capturedEvent.getS3ObjectKey()).isEqualTo(permanentImageUrl);
    }

    @Test
    @DisplayName("트랜잭션 실패 시: DB 저장 중 예외가 발생하면 이벤트가 발행되지 않는다")
    void givenDbFailure_whenUploadMemePost_thenEventIsNotPublished() {
        // given
        Long userId = 1L;
        MemePostUploadRequest request = new MemePostUploadRequest("temp/image.jpg",  List.of(1L, 2L));
        FileMeta mockFileMeta = FileMeta.builder()
                .originalFilename("image.jpg")
                .size(1000L)
                .resolution(new Resolution(300, 300))
                .extension("jpg")
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(User.builder().build()));
        given(fileMetaRepository.findByFileUrl(anyString())).willReturn(Optional.of(mockFileMeta));
        given(fileStorageService.moveFileToPermanent(any(), any())).willReturn("images/memes/permanent-image.jpg");
        given(memePostRepository.save(any(MemePost.class)))
                .willThrow(new RuntimeException("DB 저장 실패!"));

        // when & then
        assertThrows(RuntimeException.class, () -> {
            memePostService.uploadMemePost(request, userId);
        });

        verify(eventPublisher, never()).publishEvent(any());
    }

//
//    @Test
//    void uploadMemePost() {
//        //given
//        String tempImageUrl = "temps/image.png";
//        String permanentImageUrl = "images/memes/image.png";
//        List<Long> tagIds = Arrays.asList(1L, 2L);
//        List<String> tagNames = Arrays.asList("재밌는", "귀여운");
//
//        MemePostUploadRequest memePostUploadRequest = new MemePostUploadRequest(tempImageUrl, tagIds);
//
//        MemePost spyMemePost = spy(memePost);
//        doReturn(1L).when(spyMemePost).getId();
//
//        when(userRepository.findById(1L))
//                .thenReturn(Optional.ofNullable(user));
//        when(fileMetaRepository.findByFileUrl(tempImageUrl))
//                .thenReturn(Optional.ofNullable(fileMeta));
//        when(fileStorageService.moveFileToPermanent(tempImageUrl, FileType.MEME))
//                .thenReturn(permanentImageUrl);
//        when(memePostRepository.save(memePost))
//                .thenReturn(memePost);
//        when(memePostTagService.applyTagsToPost(tagIds, memePost))
//                .thenReturn(tagNames);
//
//        //when
//        MemePostUploadResponse result = memePostService.uploadMemePost(memePostUploadRequest, 1L);
//
//        //then
//        Assertions.assertThat(result).isNotNull();
//        Assertions.assertThat(result.getImageUrl()).isEqualTo(permanentImageUrl);
//        Assertions.assertThat(result.getTags()).isEqualTo(tagNames);
//    }

//    @Test
//    void getMemePost() {
//        //given
//        Long memePostId = 1L;
//        Optional<Long> userId = Optional.of(1L);
//
//        MemePost spyMemePost = spy(memePost);
//        doReturn(1L).when(spyMemePost).getId();
//
//        when( memePostRepository.findByIdWithTags(memePostId))
//                .thenReturn(Optional.ofNullable(memePost));
//        when(memePostLikeRepository.existsByMemePostIdAndUserId(memePost.getId(), userId.get()))
//                .thenReturn(true);
//
//        //when
//        MemePostGetResponse result = memePostService.getMemePost(memePostId, userId);
//
//        //then
//        verify(memePostViewCountService).incrementViewCount(memePostId);
//        Assertions.assertThat(result).isNotNull();
//        Assertions.assertThat(result.getId()).isEqualTo(memePostId);
//        Assertions.assertThat(result.getImageUrl()).isEqualTo("/image/test.png");
//    }

    @Test
    void getMemePostRedis() {
    }

    @Test
    void getMemePostsWithLikeInfo() {
    }

    @Test
    void getRecommendedPostsWithLikeInfo() {
    }

    @Test
    void softDelete() {
    }

    @Test
    void download() {
    }

    @Test
    void getMemePostsByAuthorNameWithLikeInfo() {
    }

    @Test
    void getRankedPostsAllPeriod() {
    }

    @Test
    void getRankedPostsWithPeriod() {
    }
}