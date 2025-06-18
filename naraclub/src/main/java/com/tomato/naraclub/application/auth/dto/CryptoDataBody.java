package com.tomato.naraclub.application.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CryptoDataBody {
    /**
     * 응답 코드 (P000: 정상)
     */
    @JsonProperty("rsp_cd")
    private String rspCd;

    /**
     * 사이트 코드 (암호화된 값)
     */
    @JsonProperty("site_code")
    private String siteCode;

    /**
     * 처리 결과 코드 (0000: 정상)
     */
    @JsonProperty("result_cd")
    private String resultCd;

    /**
     * 토큰 버전 식별자
     */
    @JsonProperty("token_version_id")
    private String tokenVersionId;

    /**
     * 암호화 토큰 값
     */
    @JsonProperty("token_val")
    private String tokenVal;

    /**
     * 토큰 유효 기간 (초)
     */
    @JsonProperty("period")
    private long period;
}