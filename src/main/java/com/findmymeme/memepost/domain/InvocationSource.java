package com.findmymeme.memepost.domain;

/**
 * 썸네일 생성 작업이 어디서 시작되었는지를 나타내는 enum
 */
public enum InvocationSource {
    /**
     * 일반 사용자의 파일 업로드로 인해 트리거된 경우
     */
    USER_UPLOAD,

    /**
     * 데이터 마이그레이션 스크립트로 인해 트리거된 경우
     */
    MIGRATION_SCRIPT,

    /**
     * 관리자가 특정 게시물을 재처리하기 위해 트리거한 경우
     */
    ADMIN_REPROCESS
}