package com.tomato.naraclub.application.auth.entity;

import com.tomato.naraclub.application.auth.dto.PassResponse;
import com.tomato.naraclub.common.audit.Audit;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.auth.entity
 * @fileName : NiceCryptoRequest
 * @date : 2025-05-30
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Entity
@Table(
    name = "t_nice_crypto_request",
    indexes = {
        @Index(name = "idx01_t_nice_crypto_request_created_at", columnList = "created_at"),
        @Index(name = "idx01_t_nice_crypto_request_req_no", columnList = "req_no"),
    }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NiceCryptoRequest extends Audit {

    @Comment("유저아이디")
    private Long memberId;

    @Comment("pass 요청 url")
    @Column(nullable = false, length = 256)
    private String passRequestUrl;

    @Comment("암호화 토큰 버전 id")
    @Column(nullable = false, length = 50)
    private String tokenVersionId;

    @Comment("서비스 요청 고유 번호 (req_no)")
    @Column(nullable = false, length = 30)
    private String reqNo;

    @Comment("요청 일시 (yyyyMMddHHmmss)")
    @Column(nullable = false, length = 14)
    private String reqDtim;

    @Comment("암호화토큰요청 API 응답 토큰 값")
    @Column(nullable = false, length = 256)
    private String tokenVal;

    @Comment("암호화토큰요청 API 응답 site_code")
    @Column(nullable = false, length = 16)
    private String siteCode;

    @Lob
    @Comment("AES 암호화된 요청 데이터")
    @Column(nullable = false)
    private String encData;

    @Lob
    @Comment("HMAC 무결성 체크 값")
    @Column(nullable = false)
    private String integrityValue;

    public PassResponse convertDTO() {
        return PassResponse.builder()
            .requestUrl(passRequestUrl)
            .encData(encData)
            .tokenVersionId(tokenVersionId)
            .integrityValue(integrityValue)
            .build();
    }
}
