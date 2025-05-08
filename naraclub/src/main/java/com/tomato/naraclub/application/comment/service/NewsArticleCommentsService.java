package com.tomato.naraclub.application.comment.service;

import com.tomato.naraclub.application.comment.dto.CommentRequest;
import com.tomato.naraclub.application.comment.dto.CommentResponse;
import com.tomato.naraclub.application.security.MemberUserDetails;
import com.tomato.naraclub.common.dto.ListDTO;
import org.springframework.data.domain.Pageable;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.comment.service
 * @fileName : NewsArticleCommentsService
 * @date : 2025-05-08
 * @description :
 * @AUTHOR : MinjaeKim
 */
public interface NewsArticleCommentsService {

    ListDTO<CommentResponse> getNewsComments(Long newsId, MemberUserDetails user, Pageable pageable);

    CommentResponse createComment(Long newsId, CommentRequest req, MemberUserDetails user);

    CommentResponse updateComment(Long newsId, Long newsCommentId, CommentRequest req, MemberUserDetails user);

    void deleteComment(Long newsId, Long newsCommentId, MemberUserDetails user);
}
