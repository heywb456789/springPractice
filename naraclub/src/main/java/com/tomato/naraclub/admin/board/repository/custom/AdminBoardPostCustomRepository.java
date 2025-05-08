package com.tomato.naraclub.admin.board.repository.custom;

import com.tomato.naraclub.admin.security.AdminUserDetails;
import com.tomato.naraclub.application.board.dto.BoardListRequest;
import com.tomato.naraclub.application.board.dto.BoardPostResponse;
import com.tomato.naraclub.common.dto.ListDTO;
import org.springframework.data.domain.Pageable;


public interface AdminBoardPostCustomRepository {

    ListDTO<BoardPostResponse> getBoardList(AdminUserDetails user, BoardListRequest request, Pageable pageable);
}
