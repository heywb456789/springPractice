package com.tomato.naraclub.application.subscription.entity;

import com.tomato.naraclub.application.member.entity.Member;
import com.tomato.naraclub.application.subscription.code.SubscriptionStatus;
import com.tomato.naraclub.application.subscription.dto.SubscriptionResponse;
import com.tomato.naraclub.common.audit.Audit;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
 * @fileName : SubsCription
 * @date : 2025-05-22
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Entity
@Table(
    name = "t_subscription_history",
    indexes = {
        @Index(name = "idx01_t_subscription_history_created_at", columnList = "created_at") // 최신순 정렬용
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SubscriptionHistory extends Audit {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Comment("실명")
    @Column(nullable = false)
    private String memberName;

    @Comment("신청 폰 번호")
    @Column(nullable = false)
    private String memberPhone;

    @Comment("진행 타입")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionStatus status;

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

    @Comment("tid= 성공일때만")
    @Column(nullable = true)
    private String tid;

    public SubscriptionResponse convertResponse(String returnUrl, String payRequestUrl) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return SubscriptionResponse.builder()
            .seq(id)
            .memberId(member.getId())
            .memberName(memberName)
            .memberPhone(memberPhone)
            .mid(mid)
            .licenseKey(licenseKey)
            .productName(productName)
            .productPrice(productPrice)
            .payMonth(payMonth)
            .payStartDate(payStartDate.format(formatter))
            .returnUrl(returnUrl)
            .requestUrl(payRequestUrl)
            .build();
    }

    public SubscriptionResponse convertResponseWithoutUrl() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return SubscriptionResponse.builder()
            .seq(id)
            .memberId(member.getId())
            .memberName(memberName)
            .memberPhone(memberPhone)
            .productName(productName)
            .productPrice(productPrice)
            .payMonth(payMonth)
            .payStartDate(payStartDate.format(formatter))
            .status(status)
            .createdAt(createdAt)
            .updatedAt(updatedAt)
            .build();
    }
}
