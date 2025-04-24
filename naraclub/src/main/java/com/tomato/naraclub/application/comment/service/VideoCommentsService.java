package com.tomato.naraclub.application.comment.service;

import com.tomato.naraclub.application.comment.dto.CommentRequest;
import com.tomato.naraclub.application.comment.dto.CommentResponse;
import com.tomato.naraclub.application.security.MemberUserDetails;
import com.tomato.naraclub.common.dto.ListDTO;
import org.springframework.data.domain.Pageable;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.comment.service
 * @fileName : VideoCommentsService
 * @date : 2025-04-24
 * @description :
 * @AUTHOR : MinjaeKim
 */
public interface VideoCommentsService {

    ListDTO<CommentResponse> getVideoComments(Long videoId, MemberUserDetails user, Pageable pageable);

    CommentResponse createComment(Long videoId, CommentRequest req, MemberUserDetails user);

    CommentResponse updateComment(Long videoId, Long videoCommentId, CommentRequest req, MemberUserDetails user);

    void deleteComment(Long videoId, Long videoCommentId, MemberUserDetails user);
}
