package com.tomato.naraclub.application.comment.repository.custom;

import com.tomato.naraclub.application.comment.dto.CommentResponse;
import com.tomato.naraclub.application.security.MemberUserDetails;
import com.tomato.naraclub.common.dto.ListDTO;
import org.springframework.data.domain.Pageable;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.comment.repository.custom
 * @fileName : VideoCommentsCustomRepository
 * @date : 2025-04-24
 * @description :
 * @AUTHOR : MinjaeKim
 */
public interface VideoCommentsCustomRepository {

    ListDTO<CommentResponse> getVideoComments(Long videoId, MemberUserDetails user, Pageable pageable);
}
