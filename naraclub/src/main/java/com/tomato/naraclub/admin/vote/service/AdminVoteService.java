package com.tomato.naraclub.admin.vote.service;

import com.tomato.naraclub.admin.security.AdminUserDetails;
import com.tomato.naraclub.admin.vote.dto.VoteRegisterRequest;
import com.tomato.naraclub.application.vote.dto.VoteListRequest;
import com.tomato.naraclub.application.vote.dto.VotePostResponse;
import com.tomato.naraclub.common.dto.ListDTO;
import com.tomato.naraclub.common.dto.ResponseDTO;
import org.springframework.data.domain.Pageable;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.vote.service
 * @fileName : AdminVoteService
 * @date : 2025-04-29
 * @description :
 * @AUTHOR : MinjaeKim
 */
public interface AdminVoteService {

    ListDTO<VotePostResponse> getVoteList(AdminUserDetails user, VoteListRequest request, Pageable pageable);

    VotePostResponse createVote(VoteRegisterRequest request, AdminUserDetails user);

    VotePostResponse getVoteDetail(Long id);

    VotePostResponse updateVote(VoteRegisterRequest request, AdminUserDetails user);
}
