package com.findmymeme.tag.service;

import com.findmymeme.exception.ErrorCode;
import com.findmymeme.exception.FindMyMemeException;
import com.findmymeme.tag.domain.PostTag;
import com.findmymeme.tag.domain.PostType;
import com.findmymeme.tag.domain.Tag;
import com.findmymeme.tag.repository.PostTagRepository;
import com.findmymeme.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PostTagService {

    private final TagRepository tagRepository;
    private final PostTagRepository postTagRepository;

    public List<String> applyTagsToPost(List<Long> tagIds, Long postId, PostType postType) {
        List<PostTag> postTags = tagIds.stream()
                .map(this::getTagById)
                .map(tag -> PostTag.builder()
                            .postId(postId)
                            .postType(postType)
                            .tag(tag)
                            .build())
                .toList();

        postTagRepository.saveAll(postTags);
        return postTags.stream()
                .map(postTag -> postTag.getTag().getName())
                .toList();
    }

    public List<String> getTagNames(Long postId, PostType postType) {
        return postTagRepository.findAllByPostIdAndPostType(postId, postType)
                .stream()
                .map(postTag -> postTag.getTag().getName())
                .toList();
    }

    private Tag getTagById(Long tagId) {
        return tagRepository.findById(tagId)
                .orElseThrow(() -> new FindMyMemeException(ErrorCode.NOT_FOUND_TAG));
    }
}
