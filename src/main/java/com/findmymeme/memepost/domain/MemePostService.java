package com.findmymeme.memepost.domain;

import com.findmymeme.exception.ErrorCode;
import com.findmymeme.exception.FindMyMemeException;
import com.findmymeme.file.domain.FileMeta;
import com.findmymeme.file.repository.FileMetaRepository;
import com.findmymeme.file.service.FileStorageService;
import com.findmymeme.memepost.dto.MemePostUploadRequest;
import com.findmymeme.memepost.dto.MemePostUploadResponse;
import com.findmymeme.tag.service.PostTagService;
import com.findmymeme.user.User;
import com.findmymeme.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.findmymeme.tag.domain.PostType.*;

@Service
@Transactional
@RequiredArgsConstructor
public class MemePostService {

    private final UserRepository userRepository;
    private final MemePostRepository memePostRepository;
    private final PostTagService postTagService;
    private final FileStorageService fileStorageService;
    private final FileMetaRepository fileMetaRepository;

    public MemePostUploadResponse uploadMemePost(MemePostUploadRequest request, Long userId) {
        User user = getUserById(userId);
        FileMeta fileMeta = findFileMetaByFileUrl(request.getImageUrl());
        String permanentImageUrl = fileStorageService.moveFileToPermanent(request.getImageUrl());
        MemePost memePost = createMemePost(permanentImageUrl, user, fileMeta);
        memePostRepository.save(memePost);
        List<String> tagNames = postTagService.applyTagsToPost(request.getTags(), memePost.getId(), MEME_POST);
        return new MemePostUploadResponse(memePost.getImageUrl(), tagNames);
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

    private FileMeta findFileMetaByFileUrl(String fileUrl) {
        return fileMetaRepository.findByFileUrl(fileUrl)
                .orElseThrow(() -> new FindMyMemeException(ErrorCode.NOT_FOUND_FILE_META));
    }

}
