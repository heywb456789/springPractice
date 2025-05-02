package com.tomato.naraclub.application.original.code;

import java.util.List;

/**
 * 콘텐츠 타입 (뉴스기사, 유튜브 롱폼, 유튜브 쇼츠)
 */
public enum OriginalType {
    NEWS_ARTICLE   ("뉴스토마토 기사"),
    YOUTUBE_VIDEO  ("롱폼"),
    YOUTUBE_SHORTS ("쇼츠");

    private final String displayName;

    OriginalType(String displayName) {
        this.displayName = displayName;
    }

    /** Thymeleaf 등에서 ${t.displayName} 으로 보여줄 한글 라벨 */
    public String getDisplayName() {
        return displayName;
    }

    /** 롱폼+쇼츠 (VIDEO 계열) 만 가져오기 */
    public static List<OriginalType> getVideoTypes() {
        return List.of(YOUTUBE_VIDEO, YOUTUBE_SHORTS);
    }

    /** 뉴스토마토 기사 타입만 가져오기 */
    public static List<OriginalType> getNewsArticleTypes() {
        return List.of(NEWS_ARTICLE);
    }
}
