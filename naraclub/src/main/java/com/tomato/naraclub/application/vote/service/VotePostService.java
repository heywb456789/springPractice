package com.tomato.naraclub.application.vote.service;

import com.tomato.naraclub.application.security.MemberUserDetails;
import com.tomato.naraclub.application.vote.dto.VoteListRequest;
import com.tomato.naraclub.application.vote.dto.VotePostResponse;
import com.tomato.naraclub.common.dto.ListDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.vote.service
 * @fileName : VotePostService
 * @date : 2025-04-23
 * @description :
 * @AUTHOR : MinjaeKim
 */
public interface VotePostService {

    ListDTO<VotePostResponse> getList(MemberUserDetails userDetails, VoteListRequest request, Pageable pageable);

    VotePostResponse getVoteDetailById(Long id, MemberUserDetails userDetails, HttpServletRequest request);

    Long createVoteRecord(Long votePostId, Long voteOptionId, MemberUserDetails user);
}
