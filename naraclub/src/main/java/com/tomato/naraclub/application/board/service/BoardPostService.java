package com.tomato.naraclub.application.board.service;

import com.tomato.naraclub.application.board.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardPostService {
    Page<BoardPostResponse> listPosts(Pageable pageable);
    BoardPostResponse getPost(Long id);
    BoardPostResponse createPost(CreateBoardPostRequest req);
    BoardPostResponse updatePost(Long id, UpdateBoardPostRequest req);
    void deletePost(Long id);
    int likePost(Long id);
}