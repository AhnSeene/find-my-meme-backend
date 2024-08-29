package com.findmymeme.file.dto;

import com.findmymeme.file.domain.FileMeta;
import lombok.Getter;

@Getter
public class FileUploadResponse {

    private String originalFilename;
    private String fileUrl;

    public FileUploadResponse(FileMeta fileMeta) {
        this.originalFilename = fileMeta.getOriginalFilename();
        this.fileUrl = fileMeta.getFileUrl();
    }

}
