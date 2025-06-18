package com.tomato.naraclub.application.subscription.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.subscription.dto
 * @fileName : SubscriptionConfirmRequest
 * @date : 2025-05-26
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionConfirmRequest {
    private Long seq;
    private Long userid;
    private Integer price;
//    private Integer point;   // 사용안함 null 임
    private String tid;

}
