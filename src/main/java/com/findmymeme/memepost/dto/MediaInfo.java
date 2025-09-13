package com.findmymeme.memepost.dto;

import com.findmymeme.memepost.domain.MediaType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class MediaInfo {
    @Schema(description = "미디어 타입 (STATIC: 정적 이미지, ANIMATED: 움직이는 이미지)", example = "ANIMATED")
    private MediaType mediaType;

    @Schema(description = "대체 텍스트 (접근성 용도)", example = "웃는 강아지.gif")
    private String altText;

    @Schema(description = "원본 소스 정보")
    private MediaSource originalSource;

    @Schema(description = "화면 크기별로 사용할 썸네일 소스 목록")
    private List<MediaSource> thumbnails;

    private static final int TABLET_BREAKPOINT_PX = 768;
    private static final int MOBILE_BREAKPOINT_PX = 0;
    private static final String MIME_TYPE_MP4 = "video/mp4";
    private static final String MIME_TYPE_IMAGE_PREFIX = "image/";


    public static MediaInfo from(MediaDataProvider dataProvider, String fileBaseUrl) {
        List<MediaSource> thumbnails = new ArrayList<>();
        MediaType originalMediaType = dataProvider.getMediaType();
        String originalExtension = dataProvider.getExtension().getValue();

        MediaSource originalSource = MediaSource.builder()
                .url(fileBaseUrl + dataProvider.getImageUrl())
                .mimeType(MIME_TYPE_IMAGE_PREFIX + originalExtension.toLowerCase())
                .minWidth(MOBILE_BREAKPOINT_PX)
                .build();

        if (originalMediaType == MediaType.ANIMATED) {
            addSourceIfPresent(thumbnails, fileBaseUrl, dataProvider.getThumbnail657Url(), MIME_TYPE_MP4, TABLET_BREAKPOINT_PX);
            addSourceIfPresent(thumbnails, fileBaseUrl, dataProvider.getThumbnail288Url(), MIME_TYPE_MP4, MOBILE_BREAKPOINT_PX);
        } else {
            String thumbnailMimeType = MIME_TYPE_IMAGE_PREFIX + originalExtension.toLowerCase();
            addSourceIfPresent(thumbnails, fileBaseUrl, dataProvider.getThumbnail657Url(), thumbnailMimeType, TABLET_BREAKPOINT_PX);
            addSourceIfPresent(thumbnails, fileBaseUrl, dataProvider.getThumbnail288Url(), thumbnailMimeType, MOBILE_BREAKPOINT_PX);
        }

        return MediaInfo.builder()
                .mediaType(originalMediaType)
                .altText(dataProvider.getOriginalFilename())
                .originalSource(originalSource)
                .thumbnails(thumbnails)
                .build();
    }

    private static void addSourceIfPresent(List<MediaSource> sources, String baseUrl, String urlPart, String mimeType, int minWidth) {
        if (urlPart != null && !urlPart.isBlank()) {
            sources.add(MediaSource.builder()
                    .url(baseUrl + urlPart)
                    .mimeType(mimeType)
                    .minWidth(minWidth)
                    .build());
        }
    }
}