package com.tomato.naraclub.application.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.auth.dto
 * @fileName : PassResponse
 * @date : 2025-05-30
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PassResponse {

    private String requestUrl;
    private String encData;
    private String tokenVersionId;
    private String integrityValue;
}
