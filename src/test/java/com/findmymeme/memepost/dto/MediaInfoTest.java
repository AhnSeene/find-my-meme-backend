package com.findmymeme.memepost.dto;

import com.findmymeme.memepost.domain.Extension;
import com.findmymeme.memepost.domain.MediaType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MediaInfoTest {

    private final String FILE_BASE_URL = "http://test.com/";

    @Test
    @DisplayName("성공: 움직이는 이미지(GIF) 데이터로 MediaInfo를 올바르게 생성한다")
    void givenAnimatedDataProvider_whenFrom_thenCreatesCorrectMediaInfo() {
        //given
        MediaDataProvider animatedProvider = new MockMediaDataProvider(
                "images/original.gif",
                "thumbnails/thumb_288.mp4",
                "thumbnails/thumb_657.mp4",
                MediaType.ANIMATED,
                Extension.GIF,
                "funny_cat.gif"
        );

        // when
        MediaInfo mediaInfo = MediaInfo.from(animatedProvider, FILE_BASE_URL);

        // then
        assertThat(mediaInfo).isNotNull();
        assertThat(mediaInfo.getMediaType()).isEqualTo(MediaType.ANIMATED);
        assertThat(mediaInfo.getAltText()).isEqualTo("funny_cat.gif");

        // 원본 소스 검증
        MediaSource originalSource = mediaInfo.getOriginalSource();
        assertThat(originalSource.getUrl()).isEqualTo(FILE_BASE_URL + "images/original.gif");
        assertThat(originalSource.getMimeType()).isEqualTo("image/gif");
        assertThat(originalSource.getMinWidth()).isZero();

        // 썸네일 검증 (GIF의 썸네일은 MP4로 생성되어야 함)
        assertThat(mediaInfo.getThumbnails()).hasSize(2);
        assertThat(mediaInfo.getThumbnails())
                .extracting(MediaSource::getMimeType)
                .containsOnly("video/mp4");

        assertThat(mediaInfo.getThumbnails())
                .extracting(MediaSource::getUrl)
                .containsExactly(
                        FILE_BASE_URL + "thumbnails/thumb_657.mp4",
                        FILE_BASE_URL + "thumbnails/thumb_288.mp4"
                );
    }

    @Test
    @DisplayName("성공: 정적 이미지(JPG) 데이터로 MediaInfo를 올바르게 생성한다")
    void givenStaticDataProvider_whenFrom_thenCreatesCorrectMediaInfo() {
        // given
        MediaDataProvider staticProvider = new MockMediaDataProvider(
                "images/original.jpg",
                "thumbnails/thumb_288.jpg",
                "thumbnails/thumb_657.jpg",
                MediaType.STATIC,
                Extension.JPG,
                "sad_dog.jpg"
        );

        // when
        MediaInfo mediaInfo = MediaInfo.from(staticProvider, FILE_BASE_URL);

        // then
        assertThat(mediaInfo).isNotNull();
        assertThat(mediaInfo.getMediaType()).isEqualTo(MediaType.STATIC);
        assertThat(mediaInfo.getAltText()).isEqualTo("sad_dog.jpg");

        // 원본 소스 검증
        assertThat(mediaInfo.getOriginalSource().getMimeType()).isEqualTo("image/jpg");

        // 썸네일 검증 (JPG의 썸네일은 JPG로 생성되어야 함)
        assertThat(mediaInfo.getThumbnails()).hasSize(2);
        assertThat(mediaInfo.getThumbnails())
                .extracting(MediaSource::getMimeType)
                .containsOnly("image/jpg");
    }


    private static class MockMediaDataProvider implements MediaDataProvider {
        private final String imageUrl;
        private final String thumbnail288Url;
        private final String thumbnail657Url;
        private final MediaType mediaType;
        private final Extension extension;
        private final String originalFilename;

        public MockMediaDataProvider(String imageUrl, String thumbnail288Url, String thumbnail657Url, MediaType mediaType, Extension extension, String originalFilename) {
            this.imageUrl = imageUrl;
            this.thumbnail288Url = thumbnail288Url;
            this.thumbnail657Url = thumbnail657Url;
            this.mediaType = mediaType;
            this.extension = extension;
            this.originalFilename = originalFilename;
        }

        @Override public String getImageUrl() { return imageUrl; }
        @Override public String getThumbnail288Url() { return thumbnail288Url; }
        @Override public String getThumbnail657Url() { return thumbnail657Url; }
        @Override public MediaType getMediaType() { return mediaType; }
        @Override public Extension getExtension() { return extension; }
        @Override public String getOriginalFilename() { return originalFilename; }
    }
}