package com.tomato.naraclub.admin.board.service;

import com.tomato.naraclub.admin.board.dto.AdminBoardDto;
import com.tomato.naraclub.admin.board.repository.AdminBoardPostRepository;
import com.tomato.naraclub.admin.security.AdminUserDetails;
import com.tomato.naraclub.application.board.dto.BoardListRequest;
import com.tomato.naraclub.application.board.dto.BoardPostResponse;
import com.tomato.naraclub.application.board.repository.BoardPostRepository;
import com.tomato.naraclub.common.dto.ListDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.board.service
 * @fileName : BoardServiceImpl
 * @date : 2025-04-28
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AdminBoardServiceImpl implements AdminBoardService {

    private final AdminBoardPostRepository boardPostRepository;

    @Override
    public ListDTO<BoardPostResponse> getBoardList(AdminUserDetails user, BoardListRequest request,
        Pageable pageable) {

        return boardPostRepository.getBoardList(user,request,pageable);
    }

    @Override
    public AdminBoardDto getBoardDetail(Long id) {
        //1) //
        return null;
    }
}
