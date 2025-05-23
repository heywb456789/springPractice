package com.tomato.naraclub.admin.point.service;

import com.tomato.naraclub.admin.point.dto.MemberRevokeParam;
import com.tomato.naraclub.admin.point.dto.PointListRequest;
import com.tomato.naraclub.admin.point.dto.PointResponse;
import com.tomato.naraclub.admin.security.AdminUserDetails;
import com.tomato.naraclub.application.member.entity.Member;
import com.tomato.naraclub.common.dto.ListDTO;
import com.tomato.naraclub.common.dto.ResponseDTO;
import org.springframework.data.domain.Pageable;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.point.service
 * @fileName : AdminPointService
 * @date : 2025-05-16
 * @description :
 * @AUTHOR : MinjaeKim
 */
public interface AdminPointService {

    ListDTO<PointResponse> getPointList(PointListRequest request, AdminUserDetails user, Pageable pageable);

    ListDTO<PointResponse> getUserPointList(PointListRequest request, Long id, AdminUserDetails user, Pageable pageable);

    Member getMember(Long id);

    Boolean memberPointsRevoke(MemberRevokeParam param, AdminUserDetails userDetails);
}
