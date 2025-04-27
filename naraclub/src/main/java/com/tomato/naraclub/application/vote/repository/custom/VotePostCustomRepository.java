package com.tomato.naraclub.application.vote.repository.custom;

import com.tomato.naraclub.application.vote.dto.VoteListRequest;
import com.tomato.naraclub.application.vote.dto.VotePostResponse;
import com.tomato.naraclub.common.dto.ListDTO;
import org.springframework.data.domain.Pageable;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.vote.repository.custom
 * @fileName : VotePostCustomRepository
 * @date : 2025-04-23
 * @description :
 * @AUTHOR : MinjaeKim
 */
public interface VotePostCustomRepository {

    ListDTO<VotePostResponse> getList(Long memberId, VoteListRequest request, Pageable pageable);
}
