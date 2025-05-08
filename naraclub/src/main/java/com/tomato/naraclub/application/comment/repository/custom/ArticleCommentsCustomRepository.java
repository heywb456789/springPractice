package com.tomato.naraclub.application.comment.repository.custom;

import com.tomato.naraclub.application.comment.dto.CommentResponse;
import com.tomato.naraclub.application.security.MemberUserDetails;
import com.tomato.naraclub.common.dto.ListDTO;
import org.springframework.data.domain.Pageable;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.comment.repository.custom
 * @fileName : NewsArticleCommentsCustomRepository
 * @date : 2025-05-08
 * @description :
 * @AUTHOR : MinjaeKim
 */
public interface ArticleCommentsCustomRepository {

    ListDTO<CommentResponse> getNewsComments(Long newsId, MemberUserDetails user, Pageable pageable);
}
