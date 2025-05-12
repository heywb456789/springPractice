package com.tomato.naraclub.admin.user.repository.custom;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tomato.naraclub.admin.security.AdminUserDetails;
import com.tomato.naraclub.admin.user.dto.AdminUserListRequest;
import com.tomato.naraclub.admin.user.dto.AdminUserResponse;
import com.tomato.naraclub.common.dto.ListDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.user.repository.custom
 * @fileName : AdminUserCustomRepositoryImpl
 * @date : 2025-05-12
 * @description :
 * @AUTHOR : MinjaeKim
 */
@RequiredArgsConstructor
public class AdminUserCustomRepositoryImpl implements AdminUserCustomRepository {

    private final JPAQueryFactory query;

    @Override
    public ListDTO<AdminUserResponse> getAdminUserList(AdminUserListRequest request,
        AdminUserDetails user, Pageable pageable) {
        return null;
    }
}
