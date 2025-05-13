package com.tomato.naraclub.admin.user.service;

import com.tomato.naraclub.admin.security.AdminUserDetails;
import com.tomato.naraclub.admin.user.dto.AppUserListRequest;
import com.tomato.naraclub.admin.user.dto.AppUserResponse;
import com.tomato.naraclub.admin.user.dto.UserLoginHistoryResponse;
import com.tomato.naraclub.admin.user.dto.UserUpdateRequest;
import com.tomato.naraclub.admin.user.entity.AuthorityHistory;
import com.tomato.naraclub.admin.user.repository.AppUserLoginHistoryRepository;
import com.tomato.naraclub.admin.user.repository.AppUserRepository;
import com.tomato.naraclub.admin.user.repository.AuthorityHistoryRepository;
import com.tomato.naraclub.application.auth.entity.MemberLoginHistory;
import com.tomato.naraclub.application.member.entity.Member;
import com.tomato.naraclub.common.code.MemberRole;
import com.tomato.naraclub.common.code.MemberStatus;
import com.tomato.naraclub.common.code.ResponseStatus;
import com.tomato.naraclub.common.dto.ListDTO;
import com.tomato.naraclub.common.exception.APIException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.user.service
 * @fileName : AppUserServiceImpl
 * @date : 2025-05-12
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService {

    private final AppUserRepository appUserRepository;
    private final AuthorityHistoryRepository authorityHistoryRepository;
    private final AppUserLoginHistoryRepository historyRepository;

    @Override
    public ListDTO<AppUserResponse> getAppUserList(AdminUserDetails user,
        AppUserListRequest request, Pageable pageable) {
        return appUserRepository.getAppUserList(user, request, pageable);
    }

    @Override
    @Transactional
    public AppUserResponse updateUserVerified(Long id, AdminUserDetails userDetails,
        UserUpdateRequest request) {

        Member member = appUserRepository.findById(id)
            .orElseThrow(()->new APIException(ResponseStatus.USER_NOT_EXIST));

        if(member.getStatus().equals(request.getStatus())){
            throw new APIException(ResponseStatus.ALREADY_MODIFIED_STATUS);
        }
        member.setStatus(request.getStatus());

        if(request.getStatus().equals(MemberStatus.ACTIVE)){
            member.setRole(MemberRole.USER_ACTIVE);
        }else{
            member.setRole(MemberRole.USER_INACTIVE);
        }

        AuthorityHistory authorityHistory = authorityHistoryRepository.save(
            AuthorityHistory.builder()
                .userId(member.getId())
                .memberStatus(request.getStatus())
                .reason(request.getReason())
                .createdBy(userDetails.getAdmin().getId())
                .updatedBy(userDetails.getAdmin().getId())
                .build()
        );

        log.debug(authorityHistory.toString());

        return member.convertAppUserResponse();
    }

    @Override
    public AppUserResponse getAppUserDetail(long id) {
        Member member = appUserRepository.findById(id)
            .orElseThrow(()->new APIException(ResponseStatus.USER_NOT_EXIST));

        return member.convertAppUserResponse();
    }

    @Override
    public Page<MemberLoginHistory> getAppUserLoginHistory(long id, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return historyRepository.findByMemberId(id, pageable);
    }
}
