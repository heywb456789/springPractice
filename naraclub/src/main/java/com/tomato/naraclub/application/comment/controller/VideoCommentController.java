package com.tomato.naraclub.application.comment.controller;

import com.tomato.naraclub.application.comment.dto.CommentRequest;
import com.tomato.naraclub.application.comment.dto.CommentResponse;
import com.tomato.naraclub.application.comment.service.VideoCommentsService;
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
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class VideoCommentController {

    private final VideoCommentsService videoCommentsService;

    @GetMapping("/{videoId}/comments")
    public ResponseDTO<ListDTO<CommentResponse>> getVideoComments(
            @PathVariable(name = "videoId") Long videoId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @AuthenticationPrincipal MemberUserDetails user
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt"));
        return ResponseDTO.ok(videoCommentsService.getVideoComments(videoId, user, pageable));
    }

    @PostMapping("/{videoId}/comments")
    public ResponseDTO<CommentResponse> create(
            @PathVariable(name = "videoId") Long videoId,
            @RequestBody CommentRequest req,
            @AuthenticationPrincipal MemberUserDetails user) {
        return ResponseDTO.ok(
                videoCommentsService.createComment(videoId, req, user)
        );
    }

    @PutMapping("/{videoId}/comments/{videoCommentId}")
    public ResponseDTO<CommentResponse> update(
            @PathVariable(name = "videoId") Long videoId,
            @PathVariable(name = "videoCommentId") Long videoCommentId,
            @RequestBody CommentRequest req,
            @AuthenticationPrincipal MemberUserDetails user) {
        return ResponseDTO.ok(
                videoCommentsService.updateComment(videoId, videoCommentId, req, user)
        );
    }

    @DeleteMapping("/{videoId}/comments/{videoCommentId}")
    public void delete(
            @PathVariable(name = "videoId") Long videoId,
            @PathVariable(name = "videoCommentId") Long videoCommentId,
            @AuthenticationPrincipal MemberUserDetails user) {
        videoCommentsService.deleteComment(videoId, videoCommentId, user);
    }
}
