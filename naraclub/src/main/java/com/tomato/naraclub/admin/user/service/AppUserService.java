package com.tomato.naraclub.admin.user.service;

import com.tomato.naraclub.admin.security.AdminUserDetails;
import com.tomato.naraclub.admin.user.dto.AppUserListRequest;
import com.tomato.naraclub.admin.user.dto.AppUserResponse;
import com.tomato.naraclub.admin.user.dto.UserUpdateRequest;
import com.tomato.naraclub.common.dto.ListDTO;
import org.springframework.data.domain.Pageable;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.user.service
 * @fileName : AppUserService
 * @date : 2025-05-12
 * @description :
 * @AUTHOR : MinjaeKim
 */
public interface AppUserService {

    ListDTO<AppUserResponse> getAppUserList(AdminUserDetails user, AppUserListRequest request, Pageable pageable);

    AppUserResponse updateUserVerified(Long id, AdminUserDetails userDetails, UserUpdateRequest request);

    AppUserResponse getAppUserDetail(long id);
}
