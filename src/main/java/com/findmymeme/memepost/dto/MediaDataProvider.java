package com.findmymeme.memepost.dto;

import com.findmymeme.memepost.domain.Extension;
import com.findmymeme.memepost.domain.MediaType;

/**
 * MediaInfo를 생성하는 데 필요한 데이터 제공자 인터페이스.
 * 이 인터페이스는 엔티티나 DTO가 직접 구현하는 대신, 어댑터 패턴을 통해 사용됩니다.
 */
public interface MediaDataProvider {
    String getImageUrl();
    String getThumbnail288Url();
    String getThumbnail657Url();
    MediaType getMediaType();
    Extension getExtension();
    String getOriginalFilename();
}