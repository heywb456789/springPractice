package com.tomato.naraclub.application.subscription.controller;

import com.tomato.naraclub.application.subscription.dto.SubscriptionConfirmRequest;
import com.tomato.naraclub.application.subscription.service.SubscriptionService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.subscription.controller
 * @fileName : SubscriptionController
 * @date : 2025-05-22
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Controller
@RequestMapping("/api/subscription")
@RequiredArgsConstructor
@Slf4j
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    //1회차 결제 결과 수신 페이지
    @PostMapping("/confirm")
    public String subscribeConfirmRedirect(
        @ModelAttribute SubscriptionConfirmRequest requestParams
    ) {
        subscriptionService.updateSubscriptionConfirmInfo(requestParams);
        return "redirect:/main/main.html"; // 또는 외부 URL 가능
    }
}
