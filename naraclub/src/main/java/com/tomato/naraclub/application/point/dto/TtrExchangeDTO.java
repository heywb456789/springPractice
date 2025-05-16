package com.tomato.naraclub.application.point.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.point.dto
 * @fileName : TtrExchangeDTO
 * @date : 2025-05-16
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TtrExchangeDTO {
    /**
     * {"code":"200",
     * "data":{
     *      "amount_to_transfer":0.1,
     *      "fee":0.0,
     *      "tx_id":"a8b26fd62f77a1032535fd599148b144a554a143"
     *      },
     * "message":"success"}
     */

    private String code;
    private String message;
    private TtrExchangeSuccess data;

}
