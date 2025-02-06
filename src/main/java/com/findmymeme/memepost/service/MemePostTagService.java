package com.findmymeme.memepost.service;

import com.findmymeme.exception.ErrorCode;
import com.findmymeme.exception.FindMyMemeException;
import com.findmymeme.memepost.domain.MemePost;
import com.findmymeme.tag.domain.MemePostTag;
import com.findmymeme.tag.domain.Tag;
import com.findmymeme.memepost.repository.MemePostTagRepository;
import com.findmymeme.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MemePostTagService {

    private final TagRepository tagRepository;
    private final MemePostTagRepository memePostTagRepository;

    public List<String> applyTagsToPost(List<Long> tagIds, MemePost memePost) {
        List<MemePostTag> findPostTags = createPostTags(tagIds, memePost);
        findPostTags.forEach(memePost::addMemePostTag);
        memePostTagRepository.saveAll(findPostTags);
        return memePost.getTagNames();
    }

    public List<String> getTagNames(Long postId) {
        return memePostTagRepository.findAllByMemePostId(postId).stream()
                .map(postTag -> postTag.getTag().getName())
                .toList();
    }


    public List<Long> getTagIds(Long postId) {
        return extractTagIds(memePostTagRepository.findAllByMemePostId(postId));
    }

    private List<MemePostTag> createPostTags(List<Long> tagIds, MemePost memePost) {
        return tagIds.stream()
                .map(this::getTagById)
                .map(tag -> MemePostTag.builder()
                        .memePost(memePost)
                        .tag(tag)
                        .build())
                .toList();
    }


    private Tag getTagById(Long tagId) {
        return tagRepository.findById(tagId)
                .orElseThrow(() -> new FindMyMemeException(ErrorCode.NOT_FOUND_TAG));
    }


    private List<Long> extractTagIds(List<MemePostTag> existingPostTags) {
        return existingPostTags.stream()
                .map(postTag -> postTag.getTag().getId())
                .toList();
    }
}
