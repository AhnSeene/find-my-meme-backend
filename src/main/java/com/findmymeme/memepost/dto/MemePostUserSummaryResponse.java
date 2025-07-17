package com.findmymeme.memepost.dto;

import com.findmymeme.common.dto.UserProfileResponse;
import com.findmymeme.response.MySlice;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "특정 사용자 게시물 목록 조회 응답 DTO")
@Getter
@NoArgsConstructor
public class MemePostUserSummaryResponse {
    @Schema(description = "게시물 작성자 프로필 정보")
    private UserProfileResponse user;
    @Schema(description = "작성자의 밈 게시물 목록 (Slice)")
    private MySlice<MemePostSummaryResponse> memePosts;

    @Builder
    public MemePostUserSummaryResponse(UserProfileResponse user, MySlice<MemePostSummaryResponse> memePosts) {
        this.user = user;
        this.memePosts = memePosts;
    }
}
