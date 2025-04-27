package com.tomato.naraclub.application.board.service;

import com.tomato.naraclub.application.board.dto.*;
import com.tomato.naraclub.application.security.MemberUserDetails;
import com.tomato.naraclub.common.dto.ListDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardPostService {

    ListDTO<BoardPostResponse> listPosts(MemberUserDetails userDetails, BoardListRequest request, Pageable pageable);

    BoardPostResponse updatePost(Long id, UpdateBoardPostRequest req);

    void deletePost(Long id);

    BoardPostResponse createPost(CreateBoardPostRequest req, MemberUserDetails userDetails);

    Integer likePost(Long id, MemberUserDetails userDetails);

    BoardPostResponse getPost(Long id, MemberUserDetails userDetails, HttpServletRequest request);

    Integer deleteLikePost(Long id, MemberUserDetails userDetails);
}