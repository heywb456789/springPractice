package com.tomato.naraclub.application.board.service;

import com.tomato.naraclub.application.board.dto.*;
import com.tomato.naraclub.common.dto.ListDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardPostService {

    BoardPostResponse getPost(Long id);
    BoardPostResponse createPost(CreateBoardPostRequest req);
    BoardPostResponse updatePost(Long id, UpdateBoardPostRequest req);
    void deletePost(Long id);
    int likePost(Long id);

    ListDTO<BoardPostResponse> listPosts(BoardListRequest request, Pageable pageable);
}