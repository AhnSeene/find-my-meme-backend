package com.findmymeme.tag.service;

import com.findmymeme.exception.ErrorCode;
import com.findmymeme.exception.FindMyMemeException;
import com.findmymeme.tag.domain.Tag;
import com.findmymeme.tag.dto.TagCreateRequest;
import com.findmymeme.tag.dto.TagCreateResponse;
import com.findmymeme.tag.dto.TagSummaryResponse;
import com.findmymeme.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    public TagCreateResponse createTag(TagCreateRequest request) {
        Tag parentTag = null;
        if (request.getParentTagId() != null) {
            parentTag = getParentTag(request);
        }
        Tag savedTag = tagRepository.save(createTag(request, parentTag));
        return new TagCreateResponse(savedTag);
    }

    public List<TagSummaryResponse> getTagsWithSubTags() {
        return tagRepository.findAllTagsWithSubTags()
                .stream()
                .map(TagSummaryResponse::fromEntity)
                .toList();
    }

    private Tag createTag(TagCreateRequest request, Tag parentTag) {
        return Tag.builder()
                .name(request.getName())
                .slug(request.getSlug())
                .parentTag(parentTag)
                .build();
    }

    private Tag getParentTag(TagCreateRequest request) {
        return tagRepository.findById(request.getParentTagId())
                .orElseThrow(() -> new FindMyMemeException(ErrorCode.NOT_FOUND_TAG));
    }
}
