package com.tomato.naraclub.application.subscription.service;

import com.tomato.naraclub.application.security.MemberUserDetails;
import com.tomato.naraclub.application.subscription.dto.SubscriptionConfirmRequest;
import com.tomato.naraclub.application.subscription.dto.SubscriptionRequest;
import com.tomato.naraclub.application.subscription.dto.SubscriptionResponse;
import com.tomato.naraclub.application.subscription.entity.SubscriptionHistory;
import java.util.List;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.subscription.service
 * @fileName : SubsCriptionService
 * @date : 2025-05-22
 * @description :
 * @AUTHOR : MinjaeKim
 */
public interface SubscriptionService {

    SubscriptionResponse saveSubscriptionInfo(MemberUserDetails user, SubscriptionRequest request);

    void updateSubscriptionConfirmInfo(SubscriptionConfirmRequest requestParams);

    SubscriptionResponse updateSubscriptionRecurringInfo(SubscriptionConfirmRequest requestBody);

    SubscriptionResponse getSubscriptionStatus(MemberUserDetails user);

    List<SubscriptionResponse> getPaymentHistory(MemberUserDetails user);
}
