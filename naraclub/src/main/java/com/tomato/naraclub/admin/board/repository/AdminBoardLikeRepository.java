package com.tomato.naraclub.admin.board.repository;

import com.tomato.naraclub.application.board.entity.BoardPostLike;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.board.repository
 * @fileName : AdminBoardLikeRepository
 * @date : 2025-05-20
 * @description :
 * @AUTHOR : MinjaeKim
 */
public interface AdminBoardLikeRepository extends JpaRepository<BoardPostLike, Long> {

    List<BoardPostLike> findByMemberId(Long id);
}
