package com.tomato.naraclub.application.board.repository.custom;

import com.tomato.naraclub.application.board.dto.BoardListRequest;
import com.tomato.naraclub.application.board.dto.BoardPostResponse;
import com.tomato.naraclub.common.dto.ListDTO;
import org.springframework.data.domain.Pageable;

public interface BoardPostCustomRepository {
    ListDTO<BoardPostResponse> getBoardPostList(BoardListRequest request, Pageable pageable);
}
