package com.tomato.naraclub.admin.vote.controller;

import com.tomato.naraclub.admin.security.AdminUserDetails;
import com.tomato.naraclub.admin.vote.dto.VoteDeleteRequest;
import com.tomato.naraclub.admin.vote.dto.VoteRegisterRequest;
import com.tomato.naraclub.admin.vote.service.AdminVoteService;
import com.tomato.naraclub.application.vote.dto.VotePostResponse;
import com.tomato.naraclub.common.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.vote.controller
 * @fileName : AdminVoteRestController
 * @date : 2025-04-29
 * @description :
 * @AUTHOR : MinjaeKim
 */
@RestController
@RequestMapping("/admin/vote")
@RequiredArgsConstructor
public class AdminVoteRestController {

    private final AdminVoteService adminVoteService;

    @PostMapping("/create")
    public ResponseDTO<VotePostResponse> createVote(
        @RequestBody VoteRegisterRequest request,
        @AuthenticationPrincipal AdminUserDetails user){
        return ResponseDTO.ok(adminVoteService.createVote(request, user));
    }

    @PutMapping("/update")
    public ResponseDTO<VotePostResponse> updateVote(
        @RequestBody VoteRegisterRequest request,
        @AuthenticationPrincipal AdminUserDetails user){
        return ResponseDTO.ok(adminVoteService.updateVote(request, user));
    }

    @DeleteMapping("/delete")
    public ResponseDTO<Boolean> deleteVote(
        @RequestBody VoteDeleteRequest request,
        @AuthenticationPrincipal AdminUserDetails user){
        return ResponseDTO.ok(adminVoteService.deleteVotes(request,user));
    }
}
