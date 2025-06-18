package com.tomato.naraclub.application.subscription.service;

import com.tomato.naraclub.application.member.entity.Member;
import com.tomato.naraclub.application.member.repository.MemberRepository;
import com.tomato.naraclub.application.security.MemberUserDetails;
import com.tomato.naraclub.application.subscription.code.SubscriptionStatus;
import com.tomato.naraclub.application.subscription.dto.SubscriptionConfirmRequest;
import com.tomato.naraclub.application.subscription.dto.SubscriptionRequest;
import com.tomato.naraclub.application.subscription.dto.SubscriptionResponse;
import com.tomato.naraclub.application.subscription.entity.Subscription;
import com.tomato.naraclub.application.subscription.entity.SubscriptionHistory;
import com.tomato.naraclub.application.subscription.repository.SubscriptionHistoryRepository;
import com.tomato.naraclub.application.subscription.repository.SubscriptionRepository;
import com.tomato.naraclub.common.code.MemberStatus;
import com.tomato.naraclub.common.code.ResponseStatus;
import com.tomato.naraclub.common.exception.APIException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.subscription.service
 * @fileName : SubscriptionServiceImpl
 * @date : 2025-05-22
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionHistoryRepository subscriptionHistoryRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final MemberRepository memberRepository;
    private final TtcoPointService ttcoPointService;

    @Value("${tomato.subscription.license-key}")
    private String licenseKey;

    @Value("${tomato.subscription.call-back-url}")
    private String returnUrl;

    @Value("${tomato.subscription.pay-request-url}")
    private String payRequestUrl;

    @Value("${tomato.subscription.price}")
    private int price;

    @Value("${tomato.subscription.mid}")
    private String mid;

    @Value("${tomato.subscription.payMonth}")
    private int payMonth;


    @Override
    public SubscriptionResponse getSubscriptionStatus(MemberUserDetails user) {
        Subscription subscription = subscriptionRepository.findByMemberId(user.getMember().getId())
            .orElseThrow(() -> new APIException(ResponseStatus.SUBSCRIPTION_NOT_EXIST));

        return subscription.convertDto();
    }

    @Override
    public List<SubscriptionResponse> getPaymentHistory(MemberUserDetails user) {
        List<SubscriptionHistory> subscriptionHistories = subscriptionHistoryRepository.findAllByMemberIdAndStatus(user.getMember().getId(), SubscriptionStatus.PAYMENT_SUCCESS);

        return subscriptionHistories.stream()
        .map(SubscriptionHistory::convertResponseWithoutUrl)
        .collect(Collectors.toList());
    }

    @Override
    public SubscriptionResponse saveSubscriptionInfo(MemberUserDetails user,
        SubscriptionRequest request) {

        Member member = memberRepository.findByIdAndStatus(user.getMember().getId(),
                MemberStatus.ACTIVE)
            .orElseThrow(() -> new APIException(ResponseStatus.USER_NOT_EXIST));

        SubscriptionHistory subscriptionHistory = subscriptionHistoryRepository.save(
            SubscriptionHistory.builder()
                .member(member)
                .memberName(request.getName())
                .memberPhone(request.getPhoneNumber())
                .status(SubscriptionStatus.REQUESTED)
                .mid(mid)
                .licenseKey(licenseKey)
                .productName("나라걱정 정기구독")
                .productPrice(price)
                .payMonth(payMonth)
                .payStartDate(LocalDateTime.now())
                .build());

        return subscriptionHistory.convertResponse(returnUrl, payRequestUrl);
    }

    @Override
    @Transactional
    public void updateSubscriptionConfirmInfo(SubscriptionConfirmRequest requestParams) {

        //1. 기존 신청건 정보 찾기
        SubscriptionHistory subscriptionHistory = subscriptionHistoryRepository.findById(
                requestParams.getSeq())
            .orElseThrow(() -> new APIException(ResponseStatus.SUBSCRIPTION_NOT_EXIST));

        //2. 결제 성공 이력 저장 + ONEto ONE 리얼 에도 저장
        SubscriptionHistory successHistory = subscriptionHistoryRepository.save(SubscriptionHistory
            .builder()
            .member(subscriptionHistory.getMember())
            .memberName(subscriptionHistory.getMemberName())
            .memberPhone(subscriptionHistory.getMemberPhone())
            .status(SubscriptionStatus.PAYMENT_SUCCESS)
            .mid(subscriptionHistory.getMid())
            .licenseKey(subscriptionHistory.getLicenseKey())
            .productName(subscriptionHistory.getProductName())
            .productPrice(requestParams.getPrice())
            .payMonth(subscriptionHistory.getPayMonth())
            .payStartDate(subscriptionHistory.getPayStartDate())
            .tid(requestParams.getTid())
            .build());

        Subscription success = subscriptionRepository.save(Subscription
            .builder()
            .member(subscriptionHistory.getMember())
            .totalPaidCount(1)
            .lastPaidAt(LocalDateTime.now())
            .mid(subscriptionHistory.getMid())
            .licenseKey(subscriptionHistory.getLicenseKey())
            .productName(subscriptionHistory.getProductName())
            .productPrice(requestParams.getPrice())
            .payMonth(subscriptionHistory.getPayMonth())
            .payStartDate(subscriptionHistory.getPayStartDate())
            .tid(requestParams.getTid())
            .build());

        log.info("결제 성공, {}의 구독 정보가 업데이트 되었습니다.", success.getMember().getName());

        //3. 포인트 적립

        /**
         * Path Param : type ( i : 정기 결제)
         * Query Param : userid (회원 원아이디)
         * Query Param : productName (상품명)
         * Query Param : productPrice(상품 가격)
         * Query Param : corpInfo(법인번호 => 하단 참고)
         * Query Param : paySeq(결제 고유값)
         * Query Param : pType(포인트 타입 : 0이면 적립, 1이면 사용 )
         * Query Param : point(포인트: ptype 0이면 적립포인트 1이면 사용포인트)
         */
//        ttcoPointService.trySendPoint(
//            success.getMember().getId(),
//            success.getMember().getPhoneNumber(),
//            success.getProductName(),
//            String.valueOf(success.getProductPrice()),
//            "5",                                // 법인 코드
//            String.valueOf(successHistory.getId()),      // 고유 seq
//            "0",                                      // 포인트 타입
//            "1"                                       // 적립 포인트
//        );
    }


    @Override
    @Transactional
    public SubscriptionResponse updateSubscriptionRecurringInfo(
        SubscriptionConfirmRequest requestBody) {

        Subscription subscription = subscriptionRepository.findByMemberId(requestBody.getUserid())
            .orElseThrow(() -> new APIException(ResponseStatus.SUBSCRIPTION_NOT_EXIST));

        subscriptionHistoryRepository.save(SubscriptionHistory
            .builder()
            .member(subscription.getMember())
            .memberName(subscription.getMember().getName())
            .memberPhone(subscription.getMember().getPhoneNumber())
            .status(SubscriptionStatus.PAYMENT_SUCCESS)
            .mid(subscription.getMid())
            .licenseKey(subscription.getLicenseKey())
            .productName(subscription.getProductName())
            .productPrice(subscription.getProductPrice())
            .payMonth(subscription.getPayMonth())
            .payStartDate(subscription.getPayStartDate())
            .tid(subscription.getTid())
            .build());

        subscription.setTotalPaidCount(subscription.getTotalPaidCount() + 1);
        subscription.setLastPaidAt(LocalDateTime.now());

        return subscription.convertDto();
    }
}
