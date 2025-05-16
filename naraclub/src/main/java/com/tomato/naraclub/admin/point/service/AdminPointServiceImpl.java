package com.tomato.naraclub.admin.point.service;

import com.tomato.naraclub.admin.point.dto.PointListRequest;
import com.tomato.naraclub.admin.point.dto.PointResponse;
import com.tomato.naraclub.admin.point.repository.AdminPointRepository;
import com.tomato.naraclub.admin.security.AdminUserDetails;
import com.tomato.naraclub.common.dto.ListDTO;
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

    @Override
    public ListDTO<PointResponse> getPointList(PointListRequest request, AdminUserDetails user,
        Pageable pageable) {
        return pointRepository.getPointList(request, user, pageable);
    }
}
