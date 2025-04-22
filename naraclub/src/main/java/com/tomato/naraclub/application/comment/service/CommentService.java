package com.tomato.naraclub.application.comment.service;

import com.tomato.naraclub.application.comment.dto.CommentResponse;
import com.tomato.naraclub.application.comment.dto.CommentRequest;
import com.tomato.naraclub.application.security.MemberUserDetails;
import com.tomato.naraclub.common.dto.ListDTO;
import org.springframework.data.domain.Pageable;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.comment.service
 * @fileName : CommentService
 * @date : 2025-04-22
 * @description :
 * @AUTHOR : MinjaeKim
 */
public interface CommentService {

    ListDTO<CommentResponse> getBoardPostsComments(Long postId, MemberUserDetails user, Pageable pageable);

    CommentResponse createComment(Long postId, CommentRequest req, MemberUserDetails user);

    CommentResponse updateComment(Long postId, Long commentId, CommentRequest req, MemberUserDetails user);

    void deleteComment(Long postId, Long commentId, MemberUserDetails user);

}
