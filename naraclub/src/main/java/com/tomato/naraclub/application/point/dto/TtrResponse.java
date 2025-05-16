package com.tomato.naraclub.application.point.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.point.dto
 * @fileName : TtrResponse
 * @date : 2025-05-16
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TtrResponse {

    /*200 ok / 204 이체 기록 없을경우 / 그외 에러 */
    private String code;
    private String message;
    private Double data;
}
