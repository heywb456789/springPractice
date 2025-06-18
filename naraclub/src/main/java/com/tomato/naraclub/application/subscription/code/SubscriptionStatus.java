package com.tomato.naraclub.application.subscription.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.subscription.code
 * @fileName : SubscriptionStatus
 * @date : 2025-05-22
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Getter
@RequiredArgsConstructor
public enum SubscriptionStatus {

    REQUESTED("신청됨"),              // 사용자가 구독을 신청한 초기 상태
    PAYMENT_SUCCESS("결제 성공"),     // 외부 결제 완료 후 정상 처리된 상태
    PAYMENT_FAILED("결제 실패"),      // 결제 시도는 했으나 실패
    CANCELLED("결제 취소"),           // 결제 진행 중 또는 이후 사용자가 취소한 상태
    REFUNDED("환불 완료");            // 결제 이후 환불 처리된 상태 (선택 사항)

    private final String description;
}
