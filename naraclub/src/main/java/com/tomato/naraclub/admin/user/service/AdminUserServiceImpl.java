package com.tomato.naraclub.admin.user.service;

import com.tomato.naraclub.admin.security.AdminUserDetails;
import com.tomato.naraclub.admin.user.dto.AdminUserListRequest;
import com.tomato.naraclub.admin.user.dto.AdminUserResponse;
import com.tomato.naraclub.admin.user.repository.AdminUserRepository;
import com.tomato.naraclub.common.dto.ListDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.user.service
 * @fileName : AdminUserServiceImpl
 * @date : 2025-05-12
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final AdminUserRepository adminUserRepository;

    @Override
    public ListDTO<AdminUserResponse> getAdminUserList(AdminUserDetails user,
        AdminUserListRequest request, Pageable pageable) {
        return adminUserRepository.getAdminUserList(request, user, pageable);
    }
}
