package com.tomato.naraclub.admin.point.repository.custom;

import com.tomato.naraclub.admin.point.dto.PointListRequest;
import com.tomato.naraclub.admin.point.dto.PointResponse;
import com.tomato.naraclub.admin.security.AdminUserDetails;
import com.tomato.naraclub.common.dto.ListDTO;
import org.springframework.data.domain.Pageable;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.point.repository.custom
 * @fileName : AdminPointCustomRepository
 * @date : 2025-05-16
 * @description :
 * @AUTHOR : MinjaeKim
 */
public interface AdminPointCustomRepository {

    ListDTO<PointResponse> getPointList(PointListRequest request, AdminUserDetails user, Pageable pageable);
}
