package com.tomato.naraclub.application.subscription.dto;

import com.tomato.naraclub.application.subscription.code.SubscriptionStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.subscription.dto
 * @fileName : SubscriptionResponse
 * @date : 2025-05-22
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionResponse {

    private Long seq;
    private Long memberId;
    private String memberName;
    private String memberPhone;
    private String licenseKey;
    private String mid;
    private String returnUrl;
    private String requestUrl;
    private String productName;
    private int productPrice;
    private int payMonth;
    private int remainPayMonth;
    private SubscriptionStatus status;
    private String payStartDate;
    private LocalDateTime payedDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime nextPayDate;
}
