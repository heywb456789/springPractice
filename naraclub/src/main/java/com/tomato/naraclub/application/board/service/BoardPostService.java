package com.tomato.naraclub.application.board.service;

import com.tomato.naraclub.application.board.dto.BoardPostDetailResponse;
import com.tomato.naraclub.application.board.dto.BoardPostRequest;
import com.tomato.naraclub.application.board.dto.BoardPostSummaryResponse;

import java.util.List;

public interface BoardPostService {
    Long createPost(BoardPostRequest request);
    List<BoardPostSummaryResponse> getAllPosts();
    BoardPostDetailResponse getPostDetail(Long id);
    void likePost(Long id);
    void deletePost(Long id);
}
