package com.tomato.naraclub.application.share.code;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.share.code
 * @fileName : ShareTargetType
 * @date : 2025-05-11
 * @description :
 * @AUTHOR : MinjaeKim
 */
public enum ShareTargetType {
    VIDEO_LONG,
    VIDEO_SHORT,
    NEWS,
    BOARD,
    VOTE;

    public static ShareTargetType fromString(String type) {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("ShareTargetType을 파싱할 수 없습니다: null 또는 빈 문자열");
        }
        try {
            return ShareTargetType.valueOf(type.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("알 수 없는 ShareTargetType: " + type, e);
        }
    }
}
