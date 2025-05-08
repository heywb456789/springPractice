package com.tomato.naraclub.application.comment.controller;

import com.tomato.naraclub.application.comment.dto.CommentRequest;
import com.tomato.naraclub.application.comment.dto.CommentResponse;
import com.tomato.naraclub.application.comment.service.NewsArticleCommentsService;
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
 * @fileName : ArticleCommentController
 * @date : 2025-05-08
 * @description :
 * @AUTHOR : MinjaeKim
 */
@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class ArticleCommentController {

    private final NewsArticleCommentsService articleCommentsService;

    @GetMapping("/{newsId}/comments")
    public ResponseDTO<ListDTO<CommentResponse>> getNewsComments(
            @PathVariable(name = "newsId") Long newsId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @AuthenticationPrincipal MemberUserDetails user
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt"));
        return ResponseDTO.ok(articleCommentsService.getNewsComments(newsId, user, pageable));
    }

    @PostMapping("/{newsId}/comments")
    public ResponseDTO<CommentResponse> create(
            @PathVariable(name = "newsId") Long newsId,
            @RequestBody CommentRequest req,
            @AuthenticationPrincipal MemberUserDetails user) {
        return ResponseDTO.ok(
                articleCommentsService.createComment(newsId, req, user)
        );
    }

    @PutMapping("/{newsId}/comments/{newsCommentId}")
    public ResponseDTO<CommentResponse> update(
            @PathVariable(name = "newsId") Long newsId,
            @PathVariable(name = "newsCommentId") Long newsCommentId,
            @RequestBody CommentRequest req,
            @AuthenticationPrincipal MemberUserDetails user) {
        return ResponseDTO.ok(
                articleCommentsService.updateComment(newsId, newsCommentId, req, user)
        );
    }

    @DeleteMapping("/{newsId}/comments/{newsCommentId}")
    public void delete(
            @PathVariable(name = "newsId") Long newsId,
            @PathVariable(name = "newsCommentId") Long newsCommentId,
            @AuthenticationPrincipal MemberUserDetails user) {
        articleCommentsService.deleteComment(newsId, newsCommentId, user);
    }
}
