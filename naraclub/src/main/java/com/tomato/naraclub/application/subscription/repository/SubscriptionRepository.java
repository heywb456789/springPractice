package com.tomato.naraclub.application.subscription.repository;

import com.tomato.naraclub.application.subscription.entity.Subscription;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.subscription.repository
 * @fileName : SubscriptionRepository
 * @date : 2025-05-26
 * @description :
 * @AUTHOR : MinjaeKim
 */
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    Optional<Subscription> findByMemberId(Long userid);
}
