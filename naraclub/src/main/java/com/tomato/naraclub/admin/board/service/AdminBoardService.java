package com.tomato.naraclub.admin.board.service;

import com.tomato.naraclub.admin.board.dto.AdminBoardDto;
import com.tomato.naraclub.admin.security.AdminUserDetails;
import com.tomato.naraclub.application.board.dto.BoardListRequest;
import com.tomato.naraclub.application.board.dto.BoardPostResponse;
import com.tomato.naraclub.application.comment.dto.CommentRequest;
import com.tomato.naraclub.common.dto.ListDTO;
import org.springframework.data.domain.Pageable;


public interface AdminBoardService {

    AdminBoardDto getBoardDetail(Long id);

    ListDTO<BoardPostResponse> getBoardList(AdminUserDetails user, BoardListRequest request, Pageable pageable);

    Boolean deleteBoard(Long id);

    Boolean updateComment(Long id, CommentRequest request);

    Boolean deleteComment(Long id);
}
