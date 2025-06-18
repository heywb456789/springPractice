package com.tomato.naraclub.application.subscription.entity;

import static jakarta.persistence.FetchType.LAZY;

import com.tomato.naraclub.application.member.entity.Member;
import com.tomato.naraclub.application.subscription.code.SubscriptionStatus;
import com.tomato.naraclub.application.subscription.dto.SubscriptionResponse;
import com.tomato.naraclub.common.audit.Audit;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.subscription.entity
 * @fileName : Subscription
 * @date : 2025-05-26
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Entity
@Table(
    name = "t_subscription",
    indexes = {
        @Index(name = "idx01_t_subscription_created_at", columnList = "created_at") // 최신순 정렬용
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Subscription extends Audit {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Comment("결제 회차 (시작 1)")
    private int totalPaidCount;

    @Comment("마지막 결제일")
    private LocalDateTime lastPaidAt;

    @Comment("mid")
    @Column(nullable = false)
    private String mid;

    @Comment("licenseKey")
    @Column(nullable = false, length = 300)
    private String licenseKey;

    @Comment("상품 이름")
    @Column(nullable = false)
    private String productName;

    @Comment("상품 가격")
    @Column(nullable = false)
    private int productPrice;

    @Comment("결제 개월수")
    @Column(nullable = false)
    private int payMonth;

    @Comment("결제 시작일")
    @Column(nullable = false)
    private LocalDateTime payStartDate;

    @Comment("tid")
    @Column(nullable = false, length = 300)
    private String tid;

    public SubscriptionResponse convertDto() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return SubscriptionResponse.builder()
            .seq(id)
            .memberId(member.getId())
            .memberName(member.getName())
            .memberPhone(member.getPhoneNumber())
            .mid(mid)
            .licenseKey(licenseKey)
            .productName(productName)
            .productPrice(productPrice)
            .payMonth(payMonth)
            .payStartDate(payStartDate.format(formatter))
            .payedDate(payStartDate)
            .nextPayDate(payStartDate.plusMonths(1))
            .remainPayMonth(payMonth - totalPaidCount)
            .status(SubscriptionStatus.PAYMENT_SUCCESS)
            .build();
    }
}
