package com.tomato.naraclub.application.point.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.point.code
 * @fileName : PointType
 * @date : 2025-05-14
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Getter
@RequiredArgsConstructor
public enum PointType {
    SHARE_VOTE("투표 공유" , 1),
    SHARE_BOARD("자유게시판 공유", 1),
    SHARE_NEWS("뉴스 공유", 1),
    SHARE_VIDEO_LONG("롱폼 공유", 1),
    SHARE_VIDEO_SHORT("숏폼 공유", 1),
    REFERRAL_CODE_ENTRY("초대코드 입력 보상", 10),
    REFERRAL_INVITER_BONUS("초대코드 제공 보상", 10),
    WRITE_BOARD("자유게시판 작성",1),
    WRITE_BOARD_COMMENT("자유게시판 댓글",1),
    WRITE_NEWS_COMMENT("뉴스 댓글",1),
    WRITE_VIDEO_LONG_COMMENT("오리지날 롱폼 댓글",1),
    WRITE_VIDEO_SHORT_COMMENT("오리지날 숏폼 댓글",1),
    WRITE_VOTE_COMMENT("투표 댓글",1),
    APPLY_VOTE("투표 참여", 1)
    ;

    private final String displayName;
    private final int amount;
}
