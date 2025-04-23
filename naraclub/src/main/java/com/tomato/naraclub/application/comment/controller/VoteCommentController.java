package com.tomato.naraclub.application.comment.controller;

import com.tomato.naraclub.application.comment.dto.CommentResponse;
import com.tomato.naraclub.application.comment.dto.CommentRequest;
import com.tomato.naraclub.application.comment.service.CommentService;
import com.tomato.naraclub.application.comment.service.VoteCommentService;
import com.tomato.naraclub.application.security.MemberUserDetails;
import com.tomato.naraclub.common.dto.ListDTO;
import com.tomato.naraclub.common.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.comment.controller
 * @fileName : CommentController
 * @date : 2025-04-22
 * @description :
 * @AUTHOR : MinjaeKim
 */
@RestController
@RequestMapping("/api/vote/posts")
@RequiredArgsConstructor
public class VoteCommentController {

    private final VoteCommentService voteCommentService;

    @GetMapping("/{votePostId}/comments")
    public ResponseDTO<ListDTO<CommentResponse>> getVotePostsComments(
            @PathVariable(name = "votePostId") Long votePostId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @AuthenticationPrincipal MemberUserDetails user
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt"));
        return ResponseDTO.ok(voteCommentService.getVotePostsComments(votePostId, user, pageable));
    }

    @PostMapping("/{votePostId}/comments")
    public ResponseDTO<CommentResponse> create(
            @PathVariable(name = "votePostId") Long votePostId,
            @RequestBody CommentRequest req,
            @AuthenticationPrincipal MemberUserDetails user) {
        return ResponseDTO.ok(
                voteCommentService.createComment(votePostId, req, user)
        );
    }

    @PutMapping("/{votePostId}/comments/{voteCommentId}")
    public ResponseDTO<CommentResponse> update(
            @PathVariable(name = "votePostId") Long votePostId,
            @PathVariable(name = "voteCommentId") Long voteCommentId,
            @RequestBody CommentRequest req,
            @AuthenticationPrincipal MemberUserDetails user) {
        return ResponseDTO.ok(
                voteCommentService.updateComment(votePostId, voteCommentId, req, user)
        );
    }

    @DeleteMapping("/{votePostId}/comments/{voteCommentId}")
    public void delete(
            @PathVariable(name = "votePostId") Long votePostId,
            @PathVariable(name = "voteCommentId") Long voteCommentId,
            @AuthenticationPrincipal MemberUserDetails user) {
        voteCommentService.deleteComment(votePostId, voteCommentId, user);
    }
}
