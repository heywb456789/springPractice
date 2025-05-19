package com.tomato.naraclub.admin.point.service;

import com.tomato.naraclub.admin.point.dto.MemberRevokeParam;
import com.tomato.naraclub.admin.point.dto.PointListRequest;
import com.tomato.naraclub.admin.point.dto.PointResponse;
import com.tomato.naraclub.admin.point.repository.AdminPointRepository;
import com.tomato.naraclub.admin.security.AdminUserDetails;
import com.tomato.naraclub.application.member.entity.Member;
import com.tomato.naraclub.application.member.repository.MemberRepository;
import com.tomato.naraclub.application.point.code.PointStatus;
import com.tomato.naraclub.application.point.code.PointType;
import com.tomato.naraclub.application.point.entity.PointHistory;
import com.tomato.naraclub.common.code.ResponseStatus;
import com.tomato.naraclub.common.dto.ListDTO;
import com.tomato.naraclub.common.dto.ResponseDTO;
import com.tomato.naraclub.common.exception.APIException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.point.service
 * @fileName : AdminPointServiceImpl
 * @date : 2025-05-16
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminPointServiceImpl implements AdminPointService {

    private final AdminPointRepository pointRepository;
    private final MemberRepository memberRepository;

    @Override
    public ListDTO<PointResponse> getPointList(PointListRequest request, AdminUserDetails user,
        Pageable pageable) {
        return pointRepository.getPointList(request, user, pageable);
    }

    @Override
    public ListDTO<PointResponse> getUserPointList(PointListRequest request, Long id, AdminUserDetails user,
        Pageable pageable) {
        return pointRepository.getUserPointList(request, id, user, pageable);
    }

    @Override
    public Member getMember(Long id) {
        return memberRepository.findById(id)
            .orElseThrow(()->new APIException(ResponseStatus.USER_NOT_EXIST));
    }

    @Override
    @Transactional
    public Boolean memberPointsRevoke(MemberRevokeParam param,
        AdminUserDetails userDetails) {
        try {
            Member member = memberRepository.findById(param.getMemberId())
                .orElseThrow(() -> new APIException(ResponseStatus.USER_NOT_EXIST));

            PointHistory history = PointHistory.builder()
            .member(member)
            .amount(param.getAmount())
            .reason(param.getReason())
            .status(PointStatus.POINT_REVOKE)
            .type(PointType.REVOKE_POINT)
            .targetId(member.getId())
            .build();

            pointRepository.save(history);

            member.decreasePoints(param.getAmount());
        }
        catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }


        return true;
    }
}
