package com.tomato.naraclub.application.board;

import java.util.List;

public interface BoardPostService {
    Long createPost(BoardPostRequest request);
    List<BoardPostSummaryResponse> getAllPosts();
    BoardPostDetailResponse getPostDetail(Long id);
    void likePost(Long id);
    void deletePost(Long id);
}
