package com.tomato.naraclub.application.comment.repository.custom;

import com.tomato.naraclub.application.comment.dto.CommentResponse;
import com.tomato.naraclub.application.security.MemberUserDetails;
import com.tomato.naraclub.common.dto.ListDTO;
import org.springframework.data.domain.Pageable;

public interface VoteCommentsCustomRepository {
    ListDTO<CommentResponse> getBoardPostsComments(Long votePostId, MemberUserDetails user, Pageable pageable);
}
