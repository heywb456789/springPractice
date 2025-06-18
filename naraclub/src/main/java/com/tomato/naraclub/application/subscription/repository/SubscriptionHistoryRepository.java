package com.tomato.naraclub.application.subscription.repository;

import com.tomato.naraclub.application.subscription.code.SubscriptionStatus;
import com.tomato.naraclub.application.subscription.entity.SubscriptionHistory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.subscription.repository
 * @fileName : SubscriptionRepository
 * @date : 2025-05-22
 * @description :
 * @AUTHOR : MinjaeKim
 */
public interface SubscriptionHistoryRepository extends JpaRepository<SubscriptionHistory, Long> {

    List<SubscriptionHistory> findAllByMemberIdAndStatus(Long id, SubscriptionStatus subscriptionStatus);
}
