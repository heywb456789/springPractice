package com.tomato.naraclub.admin.board.repository;

import com.tomato.naraclub.admin.board.repository.custom.AdminBoardPostCustomRepository;
import com.tomato.naraclub.application.board.entity.BoardPost;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.board.repository
 * @fileName : AdminBoardPostRepository
 * @date : 2025-04-28
 * @description :
 * @AUTHOR : MinjaeKim
 */
public interface AdminBoardPostRepository extends JpaRepository<BoardPost , Long>,
    AdminBoardPostCustomRepository {

}
