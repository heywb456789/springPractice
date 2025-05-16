package com.tomato.naraclub.application.point.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.point.dto
 * @fileName : TtrExchangeSuccess
 * @date : 2025-05-16
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TtrExchangeSuccess {
/**
 * "data":{
 * "amount_to_transfer":0.1,
 * "fee":0.0,
 * "tx_id":"a8b26fd62f77a1032535fd599148b144a554a143"
 * },
 */
    private Double amount_to_transfer;
    private Double fee;
    private String tx_id;
}
