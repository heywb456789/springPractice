package com.tomato.naraclub.admin.activity.repository.custom;

import com.tomato.naraclub.admin.activity.dto.ActivityListRequest;
import com.tomato.naraclub.admin.security.AdminUserDetails;
import com.tomato.naraclub.application.member.dto.MemberActivityResponse;
import com.tomato.naraclub.common.dto.ListDTO;
import org.springframework.data.domain.Pageable;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.activity.repository.custom
 * @fileName : AdminActivityCustomRepository
 * @date : 2025-05-20
 * @description :
 * @AUTHOR : MinjaeKim
 */
public interface AdminActivityCustomRepository {

    ListDTO<MemberActivityResponse> getActivityList(ActivityListRequest request, AdminUserDetails user, Pageable pageable);
}
