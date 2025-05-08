package com.tomato.naraclub.admin.board.service;

import com.tomato.naraclub.admin.board.dto.AdminBoardDto;
import com.tomato.naraclub.admin.board.repository.AdminBoardCommentRepository;
import com.tomato.naraclub.admin.board.repository.AdminBoardPostRepository;
import com.tomato.naraclub.admin.security.AdminUserDetails;
import com.tomato.naraclub.admin.security.AdminUserDetailsService;
import com.tomato.naraclub.application.board.dto.BoardListRequest;
import com.tomato.naraclub.application.board.dto.BoardPostResponse;
import com.tomato.naraclub.application.board.entity.BoardPost;
import com.tomato.naraclub.application.board.repository.BoardPostRepository;
import com.tomato.naraclub.application.comment.dto.CommentRequest;
import com.tomato.naraclub.application.comment.entity.BoardComments;
import com.tomato.naraclub.common.code.ResponseStatus;
import com.tomato.naraclub.common.dto.ListDTO;
import com.tomato.naraclub.common.exception.APIException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
@RequiredArgsConstructor
public class AdminBoardServiceImpl implements AdminBoardService {

    private final AdminBoardPostRepository boardPostRepository;
    private final AdminBoardCommentRepository adminBoardCommentRepository;

    @Override
    public ListDTO<BoardPostResponse> getBoardList(AdminUserDetails user, BoardListRequest request,
        Pageable pageable) {

        return boardPostRepository.getBoardList(user, request, pageable);
    }

    @Override
    public AdminBoardDto getBoardDetail(Long id) {
        BoardPost post = boardPostRepository.findWithImagesById(id)
            .orElseThrow(() -> new APIException(ResponseStatus.BOARD_POST_NOT_EXIST));
        return post.convertAdminDTO();
    }

    @Override
    @Transactional
    public Boolean deleteBoard(Long id) {
        boolean exists = boardPostRepository.existsById(id);
        if (!exists) {
            // 삭제할 대상이 없으면 false
            return false;
        }

        int updated = boardPostRepository.softDeleteById(id);
        return updated > 0;
    }

    @Override
    @Transactional
    public Boolean updateComment(Long id, CommentRequest request) {
        BoardComments comment = adminBoardCommentRepository.findById(id)
            .orElseThrow(()->new APIException(ResponseStatus.DATA_NOT_FOUND));

        comment.setContent(request.getContent());

        return true;
    }

    @Override
    public Boolean deleteComment(Long id) {
        boolean exists = adminBoardCommentRepository.existsById(id);
        if (!exists) {
            return false;
        }
        int count = adminBoardCommentRepository.softDeleteById(id);

        return count > 0;
    }
}
