package com.tomato.naraclub.admin.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.auth.dto
 * @fileName : AdminAuthResponseDTO
 * @date : 2025-04-28
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminAuthResponseDTO {

    private String token;
    private String refreshToken;
    private AdminDTO admin;
}
