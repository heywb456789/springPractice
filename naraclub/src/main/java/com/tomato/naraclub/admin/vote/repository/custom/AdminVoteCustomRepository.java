package com.tomato.naraclub.admin.vote.repository.custom;

import com.tomato.naraclub.admin.security.AdminUserDetails;
import com.tomato.naraclub.application.vote.dto.VoteListRequest;
import com.tomato.naraclub.application.vote.dto.VotePostResponse;
import com.tomato.naraclub.common.dto.ListDTO;
import org.springframework.data.domain.Pageable;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.vote.repository.custom
 * @fileName : AdminVoteCustomRepository
 * @date : 2025-04-29
 * @description :
 * @AUTHOR : MinjaeKim
 */
public interface AdminVoteCustomRepository {

    ListDTO<VotePostResponse> getVoteList(AdminUserDetails user, VoteListRequest request, Pageable pageable);
}
