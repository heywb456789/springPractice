package com.tomato.naraclub.application.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.auth.dto
 * @fileName : EncryptedTokenResponse
 * @date : 2025-05-30
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EncryptedTokenResponse {

    private DataHeader dataHeader;

    private CryptoDataBody dataBody;
}
