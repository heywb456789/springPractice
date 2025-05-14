package com.tomato.naraclub.application.point.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.point.code
 * @fileName : PointStatus
 * @date : 2025-05-14
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Getter
@RequiredArgsConstructor
public enum PointStatus {
    POINT_USE("사용"),
    POINT_EARN("적립"),
    POINT_EXPIRE("만료"),
    POINT_REVOKE("회수(관리자)"),
    ;

    private final String displayName;
}
