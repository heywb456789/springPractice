package com.tomato.naraclub.admin.point.service;

import com.tomato.naraclub.admin.point.dto.PointListRequest;
import com.tomato.naraclub.admin.point.dto.PointResponse;
import com.tomato.naraclub.admin.point.repository.AdminPointRepository;
import com.tomato.naraclub.admin.security.AdminUserDetails;
import com.tomato.naraclub.application.member.entity.Member;
import com.tomato.naraclub.application.member.repository.MemberRepository;
import com.tomato.naraclub.common.code.ResponseStatus;
import com.tomato.naraclub.common.dto.ListDTO;
import com.tomato.naraclub.common.exception.APIException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
}
