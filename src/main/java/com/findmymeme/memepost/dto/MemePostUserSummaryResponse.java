package com.findmymeme.memepost.dto;

import com.findmymeme.common.dto.UserProfileResponse;
import com.findmymeme.response.MySlice;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemePostUserSummaryResponse {
    private UserProfileResponse user;
    private MySlice<MemePostSummaryResponse> memePosts;

    @Builder
    public MemePostUserSummaryResponse(UserProfileResponse user, MySlice<MemePostSummaryResponse> memePosts) {
        this.user = user;
        this.memePosts = memePosts;
    }
}
