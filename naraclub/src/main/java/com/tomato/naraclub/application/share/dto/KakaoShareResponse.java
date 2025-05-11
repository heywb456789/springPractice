package com.tomato.naraclub.application.share.dto;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.share.dto
 * @fileName : KakaoShareResponse
 * @date : 2025-05-11
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KakaoShareResponse {

    private String CHAT_TYPE;
    private String HASH_CHAT_ID;
    private Long TEMPLATE_ID;

    private String type;    // board, vote, news, video
    private Long id;        // 게시물 ID
    private Long userId;    // 공유한 회원 ID

    private Instant receivedAt;  // 서버 수신 시간
    private String resourceId;    // X-Kakao-Resource-ID 헤더
}
