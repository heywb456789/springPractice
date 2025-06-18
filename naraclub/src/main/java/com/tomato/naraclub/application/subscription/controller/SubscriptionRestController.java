package com.tomato.naraclub.application.subscription.controller;

import com.tomato.naraclub.application.security.MemberUserDetails;
import com.tomato.naraclub.application.subscription.dto.SubscriptionConfirmRequest;
import com.tomato.naraclub.application.subscription.dto.SubscriptionRequest;
import com.tomato.naraclub.application.subscription.dto.SubscriptionResponse;
import com.tomato.naraclub.application.subscription.entity.SubscriptionHistory;
import com.tomato.naraclub.application.subscription.service.SubscriptionService;
import com.tomato.naraclub.common.dto.ResponseDTO;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.subscription.controller
 * @fileName : SubscriptionController
 * @date : 2025-05-22
 * @description :
 * @AUTHOR : MinjaeKim
 */
@RestController
@RequestMapping("/api/subscription")
@RequiredArgsConstructor
@Slf4j
public class SubscriptionRestController {

    private final SubscriptionService subsCriptionService;

    @GetMapping("/status")
    public ResponseDTO<SubscriptionResponse> getSubscriptionStatus(
        @AuthenticationPrincipal MemberUserDetails user
    ){
        return ResponseDTO.ok(subsCriptionService.getSubscriptionStatus(user));
    }

    @GetMapping("/payment-history")
    public ResponseDTO<List<SubscriptionResponse>> getPaymentHistory(
        @AuthenticationPrincipal MemberUserDetails user
    ){
        return ResponseDTO.ok(subsCriptionService.getPaymentHistory(user));
    }

    //결제 신청에 대한 결제 정보 저장
    @PostMapping("/prepare")
    public ResponseDTO<SubscriptionResponse> subscribePrepare(
        @AuthenticationPrincipal MemberUserDetails user,
        @RequestBody SubscriptionRequest request
    ) {
        return ResponseDTO.ok(subsCriptionService.saveSubscriptionInfo(user, request));
    }


    //정기 결제 성공시 수신
    @PostMapping("/recurring")
    public ResponseDTO<?> subscribeRecurring(
        @RequestBody SubscriptionConfirmRequest requestBody
    ) {
        log.info("✅ [월 정기 결제] JSON 콜백 수신: {}", requestBody);
        log.warn("✅ [월 정기 결제] JSON 콜백 수신: {}", requestBody);
        log.error("✅ [월 정기 결제] JSON 콜백 수신: {}", requestBody);


        return ResponseDTO.ok(subsCriptionService.updateSubscriptionRecurringInfo(requestBody));
    }

}
