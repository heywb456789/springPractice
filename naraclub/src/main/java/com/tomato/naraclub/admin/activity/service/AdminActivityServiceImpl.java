package com.tomato.naraclub.admin.activity.service;

import com.tomato.naraclub.admin.activity.dto.ActivityListRequest;
import com.tomato.naraclub.admin.activity.dto.ActivityRequest;
import com.tomato.naraclub.admin.activity.repository.AdminActivityRepository;
import com.tomato.naraclub.admin.point.repository.AdminPointRepository;
import com.tomato.naraclub.admin.security.AdminUserDetails;
import com.tomato.naraclub.application.member.code.ActivityReviewStage;
import com.tomato.naraclub.application.member.dto.MemberActivityResponse;
import com.tomato.naraclub.application.member.entity.Member;
import com.tomato.naraclub.application.member.entity.MemberActivity;
import com.tomato.naraclub.application.member.repository.MemberRepository;
import com.tomato.naraclub.application.point.code.PointStatus;
import com.tomato.naraclub.application.point.code.PointType;
import com.tomato.naraclub.application.point.entity.PointHistory;
import com.tomato.naraclub.application.point.service.PointService;
import com.tomato.naraclub.common.code.ResponseStatus;
import com.tomato.naraclub.common.dto.ListDTO;
import com.tomato.naraclub.common.exception.APIException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminActivityServiceImpl implements AdminActivityService {

    private final AdminActivityRepository adminActivityRepository;
    private final MemberRepository memberRepository;
    private final PointService pointService;
    private final AdminPointRepository pointRepository;

    @Override
    public ListDTO<MemberActivityResponse> getActivityList(ActivityListRequest request,
        AdminUserDetails user, Pageable pageable) {
        return adminActivityRepository.getActivityList(request, user, pageable);
    }

    @Override
    @Transactional
    public Boolean approveById(Long id, ActivityRequest activityRequest) {

        MemberActivity activity = adminActivityRepository.findByIdAndStage(id, ActivityReviewStage.PENDING_REVIEW)
            .orElseThrow(()->new APIException(ResponseStatus.ACTIVITY_NOT_EXIST));

        Member member = memberRepository.findById(activity.getAuthor().getId())
                .orElseThrow(()-> new APIException(ResponseStatus.USER_NOT_EXIST));

        activity.setStage(ActivityReviewStage.APPROVED);

        pointService.awardPoints(member, PointType.valueOf(activityRequest.getPointType()), 0L);

        return true;
    }

    @Override
    @Transactional
    public Boolean rejectById(Long id, ActivityRequest activityRequest) {
        MemberActivity activity = adminActivityRepository.findByIdAndStage(id, ActivityReviewStage.PENDING_REVIEW)
            .orElseThrow(()->new APIException(ResponseStatus.ACTIVITY_NOT_EXIST));

        activity.setStage(ActivityReviewStage.REJECTED);
        activity.setReason(activityRequest.getReason());


        return true;
    }

    @Override
    @Transactional
    public Boolean bulkApprove(ActivityRequest request) {
        List<Long> activityIds = request.getActivityIds();
        if (activityIds == null || activityIds.isEmpty()) {
            throw new APIException(ResponseStatus.ACTIVITY_IDS_NOT_EXIST);
        }

        // 포인트 유형 확인
        PointType pointType;
        try {
            pointType = PointType.valueOf(request.getPointType());
        } catch (IllegalArgumentException e) {
            throw new APIException(ResponseStatus.ACTIVITY_POINT_TYPE_NOT_EXIST);
        }

        // 다수의 활동 상태 변경 및 포인트 지급
        List<MemberActivity> activities = adminActivityRepository.findAllById(activityIds);
        List<MemberActivity> approvedActivities = new ArrayList<>();
        List<PointHistory> pointHistories = new ArrayList<>();
        Map<Member, Double> memberPointsMap = new HashMap<>();

        // 대상 활동 처리
        for (MemberActivity activity : activities) {
            // 처리 대상이 아닌 항목은 건너뛰기
            if (activity.getStage() != ActivityReviewStage.PENDING_REVIEW) {
                continue;
            }

            // 활동 상태 변경
            activity.setStage(ActivityReviewStage.APPROVED);
            approvedActivities.add(activity);

            // 포인트 합산 (회원별)
            Member member = activity.getAuthor();
            double pointAmount = pointType.getAmount();
            memberPointsMap.put(member, memberPointsMap.getOrDefault(member, 0.0) + pointAmount);

            // 포인트 내역 생성
            PointHistory pointHistory = PointHistory.builder()
                    .member(member)
                    .amount(pointAmount)
                    .status(PointStatus.POINT_EARN)
                    .type(pointType)
                    .reason(pointType.getDisplayName() + " 활동내역 보상")
                    .targetId(activity.getId())
                    .build();
            pointHistories.add(pointHistory);
        }

        // 승인된 활동이 없으면 예외 발생
        if (approvedActivities.isEmpty()) {
            throw new APIException(ResponseStatus.ACTIVITY_PROCESS_LIST_NOT_EXIST);
        }

        // 활동 일괄 저장
        adminActivityRepository.saveAll(approvedActivities);

        // 회원별 포인트 증가
        for (Map.Entry<Member, Double> entry : memberPointsMap.entrySet()) {
            Member member = entry.getKey();
            Double pointAmount = entry.getValue();
            member.increasePoint(pointAmount);
            memberRepository.save(member);
        }

        // 포인트 내역 일괄 저장
        pointRepository.saveAll(pointHistories);

        // 결과 반환
        return true;
    }

    @Override
    @Transactional
    public Boolean bulkReject(ActivityRequest request) {
        List<Long> activityIds = request.getActivityIds();
        if (activityIds == null || activityIds.isEmpty()) {
            throw new APIException(ResponseStatus.ACTIVITY_PROCESS_LIST_NOT_EXIST);
        }

        // 다수의 활동 상태 변경
        List<MemberActivity> activities = adminActivityRepository.findAllById(activityIds);
        List<MemberActivity> rejectedActivities = new ArrayList<>();

        // 대상 활동 처리
        for (MemberActivity activity : activities) {
            // 처리 대상이 아닌 항목은 건너뛰기
            if (activity.getStage() != ActivityReviewStage.PENDING_REVIEW) {
                continue;
            }

            // 활동 상태 변경
            activity.setStage(ActivityReviewStage.REJECTED);
            rejectedActivities.add(activity);
        }

        // 거절된 활동이 없으면 예외 발생
        if (rejectedActivities.isEmpty()) {
            throw new APIException(ResponseStatus.ACTIVITY_PROCESS_LIST_NOT_EXIST);
        }

        // 활동 일괄 저장
        adminActivityRepository.saveAll(rejectedActivities);

        // 결과 반환
        return true;
    }
}
