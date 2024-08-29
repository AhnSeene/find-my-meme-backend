package com.findmymeme.file.exception;

import com.findmymeme.exception.ErrorCode;
import com.findmymeme.exception.FindMyMemeException;

public class FileStorageException extends FindMyMemeException {
    public FileStorageException(ErrorCode errorCode) {
        super(errorCode);
    }

    public FileStorageException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause, errorCode);
    }
}
