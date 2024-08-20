package com.findmymeme.findpost.dto;

import com.findmymeme.findpost.domain.FindStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FindPostFoundResponse {
    private FindStatus findStatus;

    public FindPostFoundResponse(FindStatus findStatus) {
        this.findStatus = findStatus;
    }
}
