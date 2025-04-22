package com.tomato.naraclub.application.board.service;

import com.tomato.naraclub.application.board.dto.*;
import com.tomato.naraclub.application.security.MemberUserDetails;
import com.tomato.naraclub.common.dto.ListDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardPostService {

    BoardPostResponse updatePost(Long id, UpdateBoardPostRequest req);
    void deletePost(Long id);


    ListDTO<BoardPostResponse> listPosts(BoardListRequest request, Pageable pageable);

    BoardPostResponse createPost(CreateBoardPostRequest req, MemberUserDetails userDetails);

    Integer likePost(Long id, MemberUserDetails userDetails);

    BoardPostResponse getPost(Long id, MemberUserDetails userDetails);

    Integer deleteLikePost(Long id, MemberUserDetails userDetails);
}