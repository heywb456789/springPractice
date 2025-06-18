package com.tomato.naraclub.admin.board.repository;

import com.tomato.naraclub.application.board.entity.BoardPostViewHistory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.board.repository
 * @fileName : AdminBoardViewRepository
 * @date : 2025-05-20
 * @description :
 * @AUTHOR : MinjaeKim
 */
public interface AdminBoardViewRepository extends JpaRepository<BoardPostViewHistory, Long> {

    List<BoardPostViewHistory> findByReaderId(Long id);
}
