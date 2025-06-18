package com.tomato.naraclub.application.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.auth.dto
 * @fileName : DataHeader
 * @date : 2025-05-30
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DataHeader {
    /**
     * 응답코드 (정상: "1200", 그 외 오류)
     */
    @JsonProperty("GW_RSLT_CD")
    private String resultCode;

    /**
     * 응답메시지 (한글 또는 영문)
     */
    @JsonProperty("GW_RSLT_MSG")
    private String resultMsg;
}
