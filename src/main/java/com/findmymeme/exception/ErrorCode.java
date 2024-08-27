package com.findmymeme.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),

    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "잘못된 입력값입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "사용자 이름 또는 비밀번호가 잘못되었습니다."),

    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "해당 유저가 존재하지 않습니다."),
    NOT_FOUND_FILE(HttpStatus.NOT_FOUND, "해당 파일이 존재하지 않습니다."),
    NOT_FOUND_FILE_META(HttpStatus.NOT_FOUND, "해당 파일정보가 존재하지 않습니다."),
    NOT_FOUND_FIND_POST(HttpStatus.NOT_FOUND, "해당 찾아줘게시글이 존재하지 않습니다."),
    NOT_FOUND_FIND_POST_COMMENT(HttpStatus.NOT_FOUND, "해당 댓글이 존재하지 않습니다."),
    NOT_FOUND_TAG(HttpStatus.NOT_FOUND, "해당 태그가 존재하지 않습니다."),
    NOT_FOUND_MEME_POST(HttpStatus.NOT_FOUND, "해당 밈게시글이 존재하지 않습니다."),

    ALREADY_EXIST_USERNAME(HttpStatus.CONFLICT, "이미 존재하는 아이디입니다."),
    ALREADY_EXIST_FILE(HttpStatus.CONFLICT, "파일이 이미 존재합니다."),
    FIND_POST_ALREADY_FOUND(HttpStatus.CONFLICT, "이미 채택된 댓글이 있습니다."),

    INVALID_COMMENT_POST_RELATION(HttpStatus.BAD_REQUEST, "댓글이 게시글과 일치하지 않습니다."),
    INVALID_PERIOD(HttpStatus.BAD_REQUEST, "지원하지 않는 기간입니다.");
    private final HttpStatus httpStatus;
    private final String message;
}
