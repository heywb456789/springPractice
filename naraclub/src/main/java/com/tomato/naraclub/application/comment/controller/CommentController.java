package com.tomato.naraclub.application.comment.controller;

import com.tomato.naraclub.application.comment.dto.CommentResponse;
import com.tomato.naraclub.application.comment.dto.CommentRequest;
import com.tomato.naraclub.application.comment.service.CommentService;
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
@RequestMapping("/api/board/posts")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/{postId}/comments")
    public ResponseDTO<ListDTO<CommentResponse>> getBoardPostsComments(
        @PathVariable(name = "postId") Long postId,
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "10") int size,
        @AuthenticationPrincipal MemberUserDetails user
        ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt"));
        return ResponseDTO.ok(commentService.getBoardPostsComments(postId, user, pageable));
    }

    @PostMapping("/{postId}/comments")
    public ResponseDTO<CommentResponse> create(
        @PathVariable(name = "postId") Long postId,
        @RequestBody CommentRequest req,
        @AuthenticationPrincipal MemberUserDetails user) {
        return ResponseDTO.ok(
            commentService.createComment(postId, req, user)
        );
    }

    @PutMapping("/{postId}/comments/{commentId}")
    public ResponseDTO<CommentResponse> update(
        @PathVariable(name = "postId") Long postId,
        @PathVariable(name = "commentId") Long commentId,
        @RequestBody CommentRequest req,
        @AuthenticationPrincipal MemberUserDetails user) {
        return ResponseDTO.ok(
            commentService.updateComment(postId, commentId, req, user)
        );
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    public void delete(
        @PathVariable(name = "postId") Long postId,
        @PathVariable(name = "commentId") Long commentId,
        @AuthenticationPrincipal MemberUserDetails user) {
        commentService.deleteComment(postId, commentId, user);
    }
}
