package com.findmymeme.memepost.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class MemePostSearchCond {

    private String username;
    private List<Long> tagIds;

    public MemePostSearchCond(String username, List<Long> tagIds) {
        this.username = username;
        this.tagIds = tagIds;
    }
}
