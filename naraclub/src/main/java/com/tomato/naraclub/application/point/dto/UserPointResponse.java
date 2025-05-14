package com.tomato.naraclub.application.point.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.point.dto
 * @fileName : UserPointResponse
 * @date : 2025-05-14
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserPointResponse {
    private String userId;
}
