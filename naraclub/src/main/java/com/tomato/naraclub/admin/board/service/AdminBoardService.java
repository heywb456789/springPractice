package com.tomato.naraclub.admin.board.service;

import com.tomato.naraclub.admin.board.dto.AdminBoardDto;
import com.tomato.naraclub.admin.security.AdminUserDetails;
import com.tomato.naraclub.application.board.dto.BoardListRequest;
import com.tomato.naraclub.application.board.dto.BoardPostResponse;
import com.tomato.naraclub.common.dto.ListDTO;
import org.springframework.data.domain.Pageable;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.board.service
 * @fileName : BoardService
 * @date : 2025-04-28
 * @description :
 * @AUTHOR : MinjaeKim
 */
public interface AdminBoardService {

    AdminBoardDto getBoardDetail(Long id);

    ListDTO<BoardPostResponse> getBoardList(AdminUserDetails user, BoardListRequest request, Pageable pageable);
}
