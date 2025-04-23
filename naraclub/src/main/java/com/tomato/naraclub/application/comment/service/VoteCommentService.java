package com.tomato.naraclub.application.comment.service;

import com.tomato.naraclub.application.comment.dto.CommentRequest;
import com.tomato.naraclub.application.comment.dto.CommentResponse;
import com.tomato.naraclub.application.security.MemberUserDetails;
import com.tomato.naraclub.common.dto.ListDTO;
import org.springframework.data.domain.Pageable;

public interface VoteCommentService {
    ListDTO<CommentResponse> getVotePostsComments(Long votePostId, MemberUserDetails user, Pageable pageable);

    CommentResponse createComment(Long votePostId, CommentRequest req, MemberUserDetails user);

    CommentResponse updateComment(Long votePostId, Long voteCommentId, CommentRequest req, MemberUserDetails user);

    void deleteComment(Long votePostId, Long voteCommentId, MemberUserDetails user);
}
