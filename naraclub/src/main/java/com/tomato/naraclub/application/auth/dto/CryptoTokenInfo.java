package com.tomato.naraclub.application.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 암호화 토큰 요청 응답과 대칭키/무결성키 생성에 필요한 정보 보관용 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CryptoTokenInfo {
    /** 요청 시 전달한 req_dtim (yyyyMMddHHmmss) */
    private String reqDtim;

    /** 요청 시 전달한 req_no (30자) */
    private String reqNo;

    /** 암호화토큰요청 API 응답의 token_val  */
    private String tokenVal;

    /** 암호화토큰요청 API 응답의 token_version_id */
    private String tokenVersionId;

    /** 암호화토큰요청 API 응답의 site_code */
    private String siteCode;
}
