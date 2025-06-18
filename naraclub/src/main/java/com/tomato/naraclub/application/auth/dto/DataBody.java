package com.tomato.naraclub.application.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.auth.dto
 * @fileName : DataBody
 * @date : 2025-05-30
 * @description :
 * @AUTHOR : MinjaeKim
 */
// DataBody.java
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DataBody {
    /** 사용자 액세스 토큰 값 */
    @JsonProperty("access_token")
    private String accessToken;

    /** access token 만료까지 남은 시간(초) */
    @JsonProperty("expires_in")
    private long expiresIn;

    /** 토큰 타입 (bearer 고정) */
    @JsonProperty("token_type")
    private String tokenType;

    /** 요청한 scope 값(기본 default) */
    @JsonProperty("scope")
    private String scope;

}

