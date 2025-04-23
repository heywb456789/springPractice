package com.tomato.naraclub.application.vote.controller;

import com.tomato.naraclub.application.board.dto.BoardListRequest;
import com.tomato.naraclub.application.security.MemberUserDetails;
import com.tomato.naraclub.application.vote.dto.VoteListRequest;
import com.tomato.naraclub.application.vote.dto.VotePostResponse;
import com.tomato.naraclub.application.vote.service.VotePostService;
import com.tomato.naraclub.common.dto.ListDTO;
import com.tomato.naraclub.common.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.vote.controller
 * @fileName : VotePostController
 * @date : 2025-04-23
 * @description :
 * @AUTHOR : MinjaeKim
 */
@RestController
@RequestMapping("/api/vote/posts")
@RequiredArgsConstructor
public class VotePostController {
    private final VotePostService voteService;

    @GetMapping
    public ResponseDTO<ListDTO<VotePostResponse>> getList(
        VoteListRequest request,
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "6") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseDTO.ok(voteService.getList(request, pageable));
    }

    @GetMapping("/{id}")
    public ResponseDTO<VotePostResponse> getDetail(@PathVariable Long id, @AuthenticationPrincipal
        MemberUserDetails userDetails) {
        return ResponseDTO.ok(voteService.getVoteDetailById(id, userDetails));
    }

    @PostMapping("/{id}/options/{optionId}")
    public ResponseDTO<Long> createVoteRecord(
        @PathVariable("id") Long votePostId,
        @PathVariable("optionId") Long voteOptionId,
        @AuthenticationPrincipal MemberUserDetails user
    ){
        return ResponseDTO.ok(voteService.createVoteRecord(votePostId, voteOptionId, user));
    }


}
