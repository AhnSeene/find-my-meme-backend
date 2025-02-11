package com.findmymeme.tag.service;

import com.findmymeme.exception.ErrorCode;
import com.findmymeme.exception.FindMyMemeException;
import com.findmymeme.findpost.domain.FindPost;
import com.findmymeme.findpost.domain.FindPostTag;
import com.findmymeme.tag.domain.Tag;
import com.findmymeme.tag.repository.FindPostTagRepository;
import com.findmymeme.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FindPostTagService {

    private final TagRepository tagRepository;
    private final FindPostTagRepository findPostTagRepository;

    public List<String> applyTagsToPost(List<Long> tagIds, FindPost findPost) {
        if (tagIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<FindPostTag> findPostTags = createPostTags(tagIds, findPost);
        findPostTagRepository.saveAll(findPostTags);
        return findPost.getTagNames();
    }

    public List<String> updateTagsToPost(List<Long> updatedTagIds, FindPost findPost) {
        List<FindPostTag> existingPostTags = getFindPostTags(findPost.getId());

        deleteRemovedTags(updatedTagIds, existingPostTags, findPost);
        addNewTags(updatedTagIds, existingPostTags, findPost);

        return findPost.getTagNames();
    }

    public List<String> getTagNames(Long postId) {
        return extractTagNames(findPostTagRepository.findAllByFindPostId(postId));
    }

    public List<Tag> getTags(Long postId) {
        return extractTags(findPostTagRepository.findAllByFindPostId(postId));
    }

    public List<FindPostTag> getFindPostTags(Long postId) {
        return findPostTagRepository.findAllByFindPostId(postId);
    }

    private List<FindPostTag> createPostTags(List<Long> tagIds, FindPost findPost) {
        return tagIds.stream()
                .map(this::getTagById)
                .map(tag -> {
                    return FindPostTag.builder()
                            .findPost(findPost)
                            .tag(tag)
                            .build();
                })
                .toList();
    }

    private void addNewTags(List<Long> updatedTagIds, List<FindPostTag> existingPostTags, FindPost findPost) {
        List<Long> existingTagIds = extractTagIds(existingPostTags);

        List<Long> newTagIds = updatedTagIds.stream()
                .filter(newTagId -> !existingTagIds.contains(newTagId))
                .toList();

        List<FindPostTag> newPostTags = createPostTags(newTagIds, findPost);
        findPostTagRepository.saveAll(newPostTags);
    }

    private void deleteRemovedTags(List<Long> updatedTagIds, List<FindPostTag> existingPostTags, FindPost findPost) {
        List<FindPostTag> tagsToDelete = existingPostTags.stream()
                .filter(postTag -> !updatedTagIds.contains(postTag.getTag().getId()))
                .toList();
        tagsToDelete.forEach(FindPostTag::clearFindPost);
        findPostTagRepository.deleteAll(tagsToDelete);
    }

    private Tag getTagById(Long tagId) {
        return tagRepository.findById(tagId)
                .orElseThrow(() -> new FindMyMemeException(ErrorCode.NOT_FOUND_TAG));
    }

    private List<String> extractTagNames(List<FindPostTag> postTags) {
        return postTags.stream()
                .map(postTag -> postTag.getTag().getName())
                .toList();
    }

    private List<Tag> extractTags(List<FindPostTag> postTags) {
        return postTags.stream()
                .map(FindPostTag::getTag)
                .toList();
    }

    private List<Long> extractTagIds(List<FindPostTag> existingPostTags) {
        return existingPostTags.stream()
                .map(postTag -> postTag.getTag().getId())
                .toList();
    }
}
