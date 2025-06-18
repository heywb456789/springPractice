package com.tomato.naraclub.admin.activity.service;

import com.tomato.naraclub.admin.activity.dto.ActivityListRequest;
import com.tomato.naraclub.admin.activity.dto.ActivityRequest;
import com.tomato.naraclub.admin.security.AdminUserDetails;
import com.tomato.naraclub.application.member.dto.MemberActivityResponse;
import com.tomato.naraclub.common.dto.ListDTO;
import org.springframework.data.domain.Pageable;

public interface AdminActivityService {

    ListDTO<MemberActivityResponse> getActivityList(ActivityListRequest request, AdminUserDetails user, Pageable pageable);

    Boolean approveById(Long id, ActivityRequest activityRequest);

    Boolean rejectById(Long id, ActivityRequest activityRequest);

    Boolean bulkApprove(ActivityRequest activityRequest);

    Boolean bulkReject(ActivityRequest activityRequest);
}
