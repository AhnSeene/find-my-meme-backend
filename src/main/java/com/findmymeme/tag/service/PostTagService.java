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
        List<PostTag> postTags = createPostTags(tagIds, postId, postType);
        postTagRepository.saveAll(postTags);
        return extractTagNames(postTags);
    }

    public List<String> updateTagsToPost(List<Long> updatedTagIds, Long postId, PostType postType) {
        List<PostTag> existingPostTags = getPostTags(postId, postType);

        deleteRemovedTags(updatedTagIds, existingPostTags);
        addNewTags(updatedTagIds, existingPostTags, postId, postType);

        return extractTagNames(getPostTags(postId, postType));
    }

    public List<String> getTagNames(Long postId, PostType postType) {
        return extractTagNames(postTagRepository.findAllByPostIdAndPostType(postId, postType));
    }

    public List<Tag> getTags(Long postId, PostType postType) {
        return extractTags(postTagRepository.findAllByPostIdAndPostType(postId, postType));
    }

    public List<PostTag> getPostTags(Long postId, PostType postType) {
        return postTagRepository.findAllByPostIdAndPostType(postId, postType);
    }

    public List<Long> getTagIds(Long postId, PostType postType) {
        return extractTagIds(postTagRepository.findAllByPostIdAndPostType(postId, postType));
    }

    private List<PostTag> createPostTags(List<Long> tagIds, Long postId, PostType postType) {
        return tagIds.stream()
                .map(this::getTagById)
                .map(tag -> PostTag.builder()
                        .postId(postId)
                        .postType(postType)
                        .tag(tag)
                        .build())
                .toList();
    }

    private void addNewTags(List<Long> updatedTagIds, List<PostTag> existingPostTags, Long postId, PostType postType) {
        List<Long> existingTagIds = extractTagIds(existingPostTags);

        List<Long> newTagIds = updatedTagIds.stream()
                .filter(newTagId -> !existingTagIds.contains(newTagId))
                .toList();

        List<PostTag> newPostTags = createPostTags(newTagIds, postId, postType);
        postTagRepository.saveAll(newPostTags);
    }

    private void deleteRemovedTags(List<Long> updatedTagIds, List<PostTag> existingPostTags) {
        List<PostTag> tagsToDelete = existingPostTags.stream()
                .filter(postTag -> !updatedTagIds.contains(postTag.getTag().getId()))
                .toList();
        postTagRepository.deleteAll(tagsToDelete);
    }

    private Tag getTagById(Long tagId) {
        return tagRepository.findById(tagId)
                .orElseThrow(() -> new FindMyMemeException(ErrorCode.NOT_FOUND_TAG));
    }

    private List<String> extractTagNames(List<PostTag> postTags) {
        return postTags.stream()
                .map(postTag -> postTag.getTag().getName())
                .toList();
    }

    private List<Tag> extractTags(List<PostTag> postTags) {
        return postTags.stream()
                .map(postTag -> postTag.getTag())
                .toList();
    }

    private List<Long> extractTagIds(List<PostTag> existingPostTags) {
        return existingPostTags.stream()
                .map(postTag -> postTag.getTag().getId())
                .toList();
    }
}
